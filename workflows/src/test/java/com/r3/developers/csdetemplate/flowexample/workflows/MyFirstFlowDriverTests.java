package com.r3.developers.csdetemplate.flowexample.workflows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.r3.developers.serializers.MemberX500NameDeserializer;
import com.r3.developers.serializers.MemberX500NameSerializer;
import net.corda.testing.driver.DriverNodes;
import net.corda.testing.driver.EachTestDriver;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.virtualnode.VirtualNodeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class MyFirstFlowDriverTests {

    /**
     * Step 1.
     * Declare member identities needed for the tests
     * As well as any other data you want to share across tests
     */

    private static final MemberX500Name alice = MemberX500Name.parse("CN=Alice, OU=Application, O=R3, L=London, C=GB");
    private static final MemberX500Name bob = MemberX500Name.parse("CN=Bob, OU=Application, O=R3, L=London, C=GB");
    private static final ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MemberX500Name.class, new MemberX500NameSerializer());
        module.addDeserializer(MemberX500Name.class, new MemberX500NameDeserializer());
        jsonMapper.registerModule(module);
    }

    private Map<MemberX500Name, VirtualNodeInfo> vNodes;

    /**
     * Step 2.
     * Declare a test driver
     * Choose between an [EachTestDriver] which will create a fresh instance for each test. Use this if you are worried about tests clashing.
     * Or an [AllTestsDriver] which will only be created once, and reused for all tests. Use this when tests can co-exist as the tests will run faster.
     */

    @SuppressWarnings("JUnitMalformedDeclaration")
    @RegisterExtension
    private final EachTestDriver driver = new DriverNodes(alice, bob).forEachTest();

    /**
     * Step 3.
     * Start the nodes
     */

    @BeforeEach
    void setup() {
        vNodes = driver.let(dsl -> {
            dsl.startNodes(Set.of(alice, bob));
            return dsl.nodesFor("workflows");
        });

        assertThat(vNodes).withFailMessage("Failed to populate vNodes").isNotEmpty();
    }


    /**
     * Step 4.
     * Write some tests.
     * The FlowDriver runs your flows, and returns the output result for you to assert on.
     */

    @Test
    void test_that_MyFirstFLow_returns_correct_message() throws JsonProcessingException {
        String flowArgs = jsonMapper.writeValueAsString(new MyFirstFlowStartArgs(bob));
        String result = driver.let(dsl ->
                dsl.runFlow(vNodes.get(alice), MyFirstFlow.class, () -> flowArgs)
        );
        assertThat(result).isEqualTo("Hello Alice, best wishes from Bob");
    }

}
