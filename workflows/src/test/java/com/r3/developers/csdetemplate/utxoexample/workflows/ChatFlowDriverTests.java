package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.r3.developers.serializers.MemberX500NameDeserializer;
import com.r3.developers.serializers.MemberX500NameSerializer;
import net.corda.testing.driver.AllTestsDriver;
import net.corda.testing.driver.DriverNodes;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.virtualnode.VirtualNodeInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatFlowDriverTests {
    /**
     * Step 1.
     * Declare member identities needed for the tests
     * As well as any other data you want to share across tests
     */

    private static final Logger logger = LoggerFactory.getLogger(ChatFlowDriverTests.class);
    private static final MemberX500Name alice = MemberX500Name.parse("CN=Alice, OU=Application, O=R3, L=London, C=GB");
    private static final MemberX500Name bob = MemberX500Name.parse("CN=Bob, OU=Application, O=R3, L=London, C=GB");
    private static final MemberX500Name notary = MemberX500Name.parse("CN=Notary, OU=Application, O=R3, L=London, C=GB");
    private static final Map<MemberX500Name, VirtualNodeInfo> vNodes = new HashMap<>();
    private static final ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MemberX500Name.class, new MemberX500NameSerializer());
        module.addDeserializer(MemberX500Name.class, new MemberX500NameDeserializer());
        jsonMapper.registerModule(module);
    }

    private String startOfTransactionId = "SHA-256D:";
    private JavaType chatMessageListType = jsonMapper.getTypeFactory().constructCollectionType(List.class, ChatStateResults.class);

    /**
     * Step 2.
     * Declare a test driver
     * Choose between an [EachTestDriver] which will create a fresh instance for each test. Use this if you are worried about tests clashing.
     * Or an [AllTestsDriver] which will only be created once, and reused for all tests. Use this when tests can co-exist as the tests will run faster.
     */

    @RegisterExtension
    private final AllTestsDriver driver = new DriverNodes(alice, bob).withNotary(notary, 1).forAllTests();

    /**
     * Step 3.
     * Start the nodes
     */

    @BeforeAll
    void setup() {
        Set<MemberX500Name> nodes = new HashSet<>(Arrays.asList(alice, bob));
        driver.run(
                dsl -> dsl.startNodes(nodes)
                        .stream().filter(it -> it.getCpiIdentifier().getName().equals("workflows"))
                        .forEach(it -> vNodes.put(it.getHoldingIdentity().getX500Name(), it))
        );

        if (vNodes.isEmpty()) fail("Failed to populate vNodes");
    }

    /**
     * Step 4.
     * Write some tests.
     * The FlowDriver runs your flows, and returns the output result for you to assert on.
     */

    @Test
    void test_that_CreateNewChatFlow_returns_correct_message() {
        CreateNewChatFlowArgs chatFlowArgs = new CreateNewChatFlowArgs("myChatName", "Hello Bob, from Alice", bob.toString());
        String result = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), CreateNewChatFlow.class, () -> jsonMapper.writeValueAsString(chatFlowArgs))
        );
        assertThat(result).contains(startOfTransactionId);
    }

    @Test
    void test_that_listChatsFlow_returns_correct_values() throws JsonProcessingException {
        // Get the current count before we start
        String listOfChatMessagesResult1 = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), ListChatsFlow.class, () -> "")
        );
        List<ChatStateResults> listOfChatMessages1 = jsonMapper.readValue(listOfChatMessagesResult1, chatMessageListType);
        Integer sizeBeforeSendingMessage = listOfChatMessages1.size();

        // Send a new message, so there is another chat in list of chats
        test_that_CreateNewChatFlow_returns_correct_message();

        // Get the latest count, and assert it has increased
        String listOfChatMessagesResult2 = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), ListChatsFlow.class, () -> "")
        );
        List<ChatStateResults> listAfterSendingMessage = jsonMapper.readValue(listOfChatMessagesResult2, chatMessageListType);
        Integer sizeAfterSendingMessage = listAfterSendingMessage.size();
        assertThat(sizeAfterSendingMessage).isGreaterThan(sizeBeforeSendingMessage);

        // Assert the response contains all the values
        ChatStateResults firstMessageInList = listAfterSendingMessage.get(0);
        assertThat(firstMessageInList).hasNoNullFieldsOrProperties();
    }

    @Test
    void test_that_UpdateChatFlow_returns_correct_values() throws JsonProcessingException {
        // Send a message
        test_that_CreateNewChatFlow_returns_correct_message();

        // List the messages and retrieve the id
        String listMessagesResult = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), ListChatsFlow.class, () -> "")
        );
        List<ChatStateResults> messageList = jsonMapper.readValue(listMessagesResult, chatMessageListType);
        UUID firstMessageId = messageList.get(messageList.size() - 1).getId();

        // Update the message
        String expectedMessage = "Updated message";
        UpdateChatFlowArgs updateChatFlowArgs = new UpdateChatFlowArgs(firstMessageId, expectedMessage);
        String updateMessageResult = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), UpdateChatFlow.class, () -> jsonMapper.writeValueAsString(updateChatFlowArgs))
        );
        assertThat(updateMessageResult).contains(startOfTransactionId);

        // List the message and validate updated message is present
        String listUpdatedMessagesResult = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), ListChatsFlow.class, () -> "")
        );
        logger.info("listUpdatedMessagesResult {}", listUpdatedMessagesResult);
        List<ChatStateResults> updatedMessageList = jsonMapper.readValue(listUpdatedMessagesResult, chatMessageListType);
        String updatedMessageValue = updatedMessageList.stream()
                .filter(it -> it.getId().equals(firstMessageId))
                .toList().get(0)
                .getMessage();
        assertThat(updatedMessageValue).isEqualTo(expectedMessage);
    }

    @Test
    void test_that_GetChatFlow_returns_correct_values() throws JsonProcessingException {
        // Alice sends a message to Bob
        test_that_CreateNewChatFlow_returns_correct_message();

        // List the messages and retrieve the id
        String listMessagesResult = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), ListChatsFlow.class, () -> "")
        );
        List<ChatStateResults> messageList = jsonMapper.readValue(listMessagesResult, chatMessageListType);
        UUID firstMessageId = messageList.get(messageList.size() - 1).getId();

        // Get the latest message
        GetChatFlowArgs getChatFlowArgs = new GetChatFlowArgs(firstMessageId, 1);
        String gatheredMessageResult1 = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), GetChatFlow.class, () -> jsonMapper.writeValueAsString(getChatFlowArgs))
        );
        JavaType messageAndSenderListType = jsonMapper.getTypeFactory().constructCollectionType(List.class, MessageAndSender.class);
        List<MessageAndSender> listOfMessages1 = jsonMapper.readValue(gatheredMessageResult1, messageAndSenderListType);
        assertThat(listOfMessages1.size()).isEqualTo(1);
        MessageAndSender message1Values = listOfMessages1.get(0);
        assertThat(message1Values.getMessageFrom()).isEqualTo(alice.toString());

        // Alice sends an updated message to Bob
        String secondMessageExpectedValue = "Hello Bob, It's Alice again";
        UpdateChatFlowArgs aliceUpdatedMessageArgs = new UpdateChatFlowArgs(firstMessageId, secondMessageExpectedValue);
        driver.run(dsl ->
                dsl.runFlow(vNodes.get(alice), UpdateChatFlow.class, () -> jsonMapper.writeValueAsString(aliceUpdatedMessageArgs))
        );

        // Get the latest message and assert the message content is updated value
        String gatheredMessageResult2 = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), GetChatFlow.class, () -> jsonMapper.writeValueAsString(getChatFlowArgs))
        );
        List<MessageAndSender> listOfMessages2 = jsonMapper.readValue(gatheredMessageResult2, messageAndSenderListType);
        assertThat(listOfMessages2.size()).isEqualTo(1);
        MessageAndSender message2Values = listOfMessages2.get(0);
        assertThat(message2Values.getMessageFrom()).isEqualTo(alice.toString());
        assertThat(message2Values.getMessage()).isEqualTo(secondMessageExpectedValue);

        // Bob sends an update message to Alice
        String thirdMessageExpectedValue = "Hello Alice, I've been busy. Bob";
        UpdateChatFlowArgs bobUpdatedMessageArgs = new UpdateChatFlowArgs(firstMessageId, thirdMessageExpectedValue);
        driver.run(dsl ->
                dsl.runFlow(vNodes.get(bob), UpdateChatFlow.class, () -> jsonMapper.writeValueAsString(bobUpdatedMessageArgs))
        );

        // Get the latest message and assert the message content is updated value
        String gatheredMessageResult3 = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), GetChatFlow.class, () -> jsonMapper.writeValueAsString(getChatFlowArgs))
        );
        List<MessageAndSender> listOfMessages3 = jsonMapper.readValue(gatheredMessageResult3, messageAndSenderListType);
        assertThat(listOfMessages3.size()).isEqualTo(1);
        MessageAndSender message3Values = listOfMessages3.get(0);
        assertThat(message3Values.getMessageFrom()).isEqualTo(bob.toString());
        assertThat(message3Values.getMessage()).isEqualTo(thirdMessageExpectedValue);

        // Get full back-chain
        GetChatFlowArgs getFullChatFlowArgs = new GetChatFlowArgs(firstMessageId, 9999);
        String gatheredAllMessagesResult = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), GetChatFlow.class, () -> jsonMapper.writeValueAsString(getFullChatFlowArgs))
        );

        logger.info("gatheredAllMessagesResult : {}", gatheredAllMessagesResult);
        List<MessageAndSender> listOfAllMessages = jsonMapper.readValue(gatheredAllMessagesResult, messageAndSenderListType);
        assertThat(listOfAllMessages.size()).isEqualTo(3);
    }
}
