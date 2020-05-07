package software.amazon.xray.samplingrule;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetSamplingRulesRequest;
import software.amazon.awssdk.services.xray.model.GetSamplingRulesResponse;
import software.amazon.awssdk.services.xray.model.SamplingRuleRecord;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;

public class ReadHandler extends BaseHandler<CallbackContext> {

    private XRayClient client;

    public ReadHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public ReadHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();
        ResourceModel responseResourceModel;

        GetSamplingRulesResponse getSamplingRulesResponse = doGetSamplingRules(requestResourceModel.getRuleName(),
                proxy, client, logger);

        SamplingRuleRecord samplingRuleRecord = getSamplingRuleRecord(requestResourceModel.getRuleARN(),
                getSamplingRulesResponse);

        if (samplingRuleRecord == null) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, requestResourceModel.getRuleARN());
        } else {
            responseResourceModel = buildResponseResourceModel(samplingRuleRecord);
        }

        logger.log(String.format("%s [%s] Read successful",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(responseResourceModel)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private GetSamplingRulesResponse doGetSamplingRules(String ruleName, AmazonWebServicesClientProxy proxy,
                                            XRayClient client, Logger logger) {
        GetSamplingRulesRequest getSamplingRulesRequest = GetSamplingRulesRequest.builder().build();
        GetSamplingRulesResponse getSamplingRulesResponse;

        try {
            getSamplingRulesResponse = proxy.injectCredentialsAndInvokeV2(getSamplingRulesRequest,
                    client::getSamplingRules);
        } catch (Exception e) {
            throw new CfnInternalFailureException();
        }

        return getSamplingRulesResponse;
    }

    private SamplingRuleRecord getSamplingRuleRecord(String ruleARN,
                                                     GetSamplingRulesResponse getSamplingRulesResponse) {
        List<SamplingRuleRecord> samplingRuleRecords = getSamplingRulesResponse.samplingRuleRecords();

        for (SamplingRuleRecord samplingRuleRecord : samplingRuleRecords) {
            if (samplingRuleRecord.samplingRule().ruleARN().equals(ruleARN)) {
                return samplingRuleRecord;
            }
        }
        return null;
    }

    private ResourceModel buildResponseResourceModel(SamplingRuleRecord samplingRuleRecord) {
        SamplingRule.SamplingRuleBuilder samplingRuleBuilder = software.amazon.xray.samplingrule.SamplingRule.builder();
        software.amazon.xray.samplingrule.SamplingRuleRecord.SamplingRuleRecordBuilder samplingRuleRecordBuilder =
                software.amazon.xray.samplingrule.SamplingRuleRecord.builder();
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();

        samplingRuleBuilder.attributes(samplingRuleRecord.samplingRule().attributes());
        samplingRuleBuilder.fixedRate(samplingRuleRecord.samplingRule().fixedRate());
        samplingRuleBuilder.host(samplingRuleRecord.samplingRule().host());
        samplingRuleBuilder.hTTPMethod(samplingRuleRecord.samplingRule().httpMethod());
        samplingRuleBuilder.priority(samplingRuleRecord.samplingRule().priority());
        samplingRuleBuilder.resourceARN(samplingRuleRecord.samplingRule().resourceARN());
        samplingRuleBuilder.reservoirSize(samplingRuleRecord.samplingRule().reservoirSize());
        samplingRuleBuilder.ruleARN(samplingRuleRecord.samplingRule().ruleARN());
        samplingRuleBuilder.ruleName(samplingRuleRecord.samplingRule().ruleName());
        samplingRuleBuilder.serviceName(samplingRuleRecord.samplingRule().serviceName());
        samplingRuleBuilder.serviceType(samplingRuleRecord.samplingRule().serviceType());
        samplingRuleBuilder.uRLPath(samplingRuleRecord.samplingRule().urlPath());
        samplingRuleBuilder.version(samplingRuleRecord.samplingRule().version());

        samplingRuleRecordBuilder.createdAt(samplingRuleRecord.createdAt().toString());
        samplingRuleRecordBuilder.modifiedAt(samplingRuleRecord.modifiedAt().toString());
        samplingRuleRecordBuilder.samplingRule(samplingRuleBuilder.build());

        // ResourceModel to be returned, should always have a primary Identifier. In this case RuleARN is the
        // primary Identifier.
        responseResourceModelBuilder.ruleARN(samplingRuleRecord.samplingRule().ruleARN());
//        responseResourceModelBuilder.samplingRuleRecord(samplingRuleRecordBuilder.build());
        responseResourceModelBuilder.samplingRule(samplingRuleBuilder.build());

        return responseResourceModelBuilder.build();
    }
}
