package software.amazon.xray.samplingrule.utils;

import software.amazon.awssdk.services.xray.model.SamplingRule;
import software.amazon.awssdk.services.xray.model.SamplingRuleRecord;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;

public class TestUtils {

    public static software.amazon.awssdk.services.xray.model.SamplingRuleRecord buildMockSamplingRuleRecord() {
        software.amazon.awssdk.services.xray.model.SamplingRuleRecord.Builder samplingRuleRecordBuilder =
                SamplingRuleRecord.builder();
        software.amazon.awssdk.services.xray.model.SamplingRule samplingRule = buildMockSamplingRule();

        samplingRuleRecordBuilder.createdAt(Instant.now());
        samplingRuleRecordBuilder.modifiedAt(Instant.now());
        samplingRuleRecordBuilder.samplingRule(samplingRule);

        return samplingRuleRecordBuilder.build();
    }

    private static software.amazon.awssdk.services.xray.model.SamplingRule buildMockSamplingRule() {
        software.amazon.awssdk.services.xray.model.SamplingRule.Builder samplingRuleBuilder = SamplingRule.builder();

        samplingRuleBuilder.host("test-host");
        samplingRuleBuilder.httpMethod("test-httpMethod");
        samplingRuleBuilder.priority(1);
        samplingRuleBuilder.resourceARN("test-resource-arn");
        samplingRuleBuilder.reservoirSize(50);
        samplingRuleBuilder.serviceType("test-service-type");
        samplingRuleBuilder.serviceName("test-service-name");
        samplingRuleBuilder.urlPath("test-url");
        samplingRuleBuilder.version(1);
        samplingRuleBuilder.ruleARN("test-ruleARN");
        samplingRuleBuilder.ruleName("test-ruleName");
        samplingRuleBuilder.fixedRate(0.1);
        samplingRuleBuilder.attributes(ImmutableMap.of("key", "value"));

        return samplingRuleBuilder.build();
    }
}
