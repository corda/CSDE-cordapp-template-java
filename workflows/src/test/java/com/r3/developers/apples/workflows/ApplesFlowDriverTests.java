package com.r3.developers.apples.workflows;

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
public class ApplesFlowDriverTests {

    private static final Logger logger = LoggerFactory.getLogger(ApplesFlowDriverTests.class);
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

    @RegisterExtension
    private final AllTestsDriver driver = new DriverNodes(alice, bob).withNotary(notary, 1).forAllTests();

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

    @Test
    void test_that_CreateAndIssueAppleStampFlow_returns_correct_message() {
        UUID stampId = createAndIssueAppleStamp("Stamp # 0001", bob, alice);
        logger.info("result: {}", stampId);
        assertThat(stampId).isNotNull();
        assertThat(stampId.toString()).hasSize(36); // standard UUID length
    }

    @Test
    void test_that_PackageApplesFlow_is_successful() {
        packageApples("Basket of apples # 0001", 100, alice);
        // flow has no return value to assert, if no exceptions are thrown test is successful
    }

    @Test
    void test_that_RedeemApplesFlow_is_successful() {
        UUID stampId = createAndIssueAppleStamp("Stamp # 0002", bob, alice);
        packageApples("Basket of apples # 0002", 350, alice);
        RedeemApplesRequest redeemApplesFlowArgs = new RedeemApplesRequest(bob, stampId);
        driver.run(dsl ->
                dsl.runFlow(vNodes.get(alice), RedeemApplesFlow.class, () -> jsonMapper.writeValueAsString(redeemApplesFlowArgs))
        );
        // flow has no return value to assert, if no exceptions are thrown test is successful
    }

    private void packageApples(String description, int weight, MemberX500Name packer) {
        PackageApplesRequest packageApplesFlowArgs = new PackageApplesRequest(description, weight);
        driver.run(dsl ->
                dsl.runFlow(vNodes.get(packer), PackageApplesFlow.class, () -> jsonMapper.writeValueAsString(packageApplesFlowArgs))
        );
    }

    private UUID createAndIssueAppleStamp(String description, MemberX500Name member, MemberX500Name issuer) {
        CreateAndIssueAppleStampRequest createAndIssueFlowArgs = new CreateAndIssueAppleStampRequest(description, member);
        String result = driver.let(dsl ->
                dsl.runFlow(vNodes.get(issuer), CreateAndIssueAppleStampFlow.class, () -> jsonMapper.writeValueAsString(createAndIssueFlowArgs))
        );
        return UUID.fromString(result);
    }
}