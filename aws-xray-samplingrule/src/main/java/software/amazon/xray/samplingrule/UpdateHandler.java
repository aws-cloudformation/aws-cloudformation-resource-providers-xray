package software.amazon.xray.samplingrule;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.SamplingRuleUpdate;
import software.amazon.awssdk.services.xray.model.UpdateSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.UpdateSamplingRuleResponse;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import com.amazonaws.util.StringUtils;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_NOT_FOUND_ERR_MESSAGE = "DOES NOT EXIST";
    private XRayClient client;

    public UpdateHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public UpdateHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();

        ResourceModel responseResourceModel = doUpdateSamplingRule(proxy, requestResourceModel, client);

        logger.log(String.format("%s [%s] update successfully",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(responseResourceModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doUpdateSamplingRule(AmazonWebServicesClientProxy proxy, ResourceModel model,
                                               XRayClient client) {
        UpdateSamplingRuleRequest updateSamplingRuleRequest = buildUpdateSamplingRuleRequest(model);
        UpdateSamplingRuleResponse updateSamplingRuleResponse;
        try {
            updateSamplingRuleResponse = proxy.injectCredentialsAndInvokeV2(updateSamplingRuleRequest,
                    client::updateSamplingRule);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains(RESOURCE_NOT_FOUND_ERR_MESSAGE)) {
                throw new CfnNotFoundException(e);
            } else {
                throw new CfnInvalidRequestException(updateSamplingRuleRequest.toString(), e);
            }
        }

        return buildResponseResourceModel(updateSamplingRuleResponse);
    }

    private UpdateSamplingRuleRequest buildUpdateSamplingRuleRequest(ResourceModel model) {
        SamplingRuleUpdate.Builder samplingRuleUpdateBuilder = SamplingRuleUpdate.builder();
        UpdateSamplingRuleRequest.Builder updateSamplingRuleRequestBuilder = UpdateSamplingRuleRequest.builder();

        samplingRuleUpdateBuilder.attributes(model.getSamplingRule().getAttributes());
        samplingRuleUpdateBuilder.fixedRate(model.getSamplingRule().getFixedRate());
        samplingRuleUpdateBuilder.host(model.getSamplingRule().getHost());
        samplingRuleUpdateBuilder.httpMethod(model.getSamplingRule().getHTTPMethod());
        samplingRuleUpdateBuilder.priority(model.getSamplingRule().getPriority());
        samplingRuleUpdateBuilder.reservoirSize(model.getSamplingRule().getReservoirSize());
        samplingRuleUpdateBuilder.resourceARN(model.getSamplingRule().getResourceARN());
        if (StringUtils.isNullOrEmpty(model.getRuleARN())) {
            samplingRuleUpdateBuilder.ruleName(model.getSamplingRule().getRuleName());
        } else {
            samplingRuleUpdateBuilder.ruleARN(model.getRuleARN());
        }
        samplingRuleUpdateBuilder.serviceName(model.getSamplingRule().getServiceName());
        samplingRuleUpdateBuilder.serviceType(model.getSamplingRule().getServiceType());
        samplingRuleUpdateBuilder.urlPath(model.getSamplingRule().getURLPath());
        SamplingRuleUpdate samplingRuleUpdate = samplingRuleUpdateBuilder.build();

        return updateSamplingRuleRequestBuilder
                .samplingRuleUpdate(samplingRuleUpdate)
                .build();
    }

    private ResourceModel buildResponseResourceModel(UpdateSamplingRuleResponse updateSamplingRuleResponse) {
        SamplingRule.SamplingRuleBuilder samplingRuleBuilder = software.amazon.xray.samplingrule.SamplingRule.builder();
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();

        samplingRuleBuilder.attributes(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().attributes());
        samplingRuleBuilder.fixedRate(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().fixedRate());
        samplingRuleBuilder.host(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().host());
        samplingRuleBuilder.hTTPMethod(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().httpMethod());
        samplingRuleBuilder.priority(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().priority());
        samplingRuleBuilder.resourceARN(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().resourceARN());
        samplingRuleBuilder.reservoirSize(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().reservoirSize());
        samplingRuleBuilder.ruleARN(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleARN());
        samplingRuleBuilder.ruleName(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleName());
        samplingRuleBuilder.serviceName(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().serviceName());
        samplingRuleBuilder.serviceType(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().serviceType());
        samplingRuleBuilder.uRLPath(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().urlPath());
        samplingRuleBuilder.version(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().version());

        // ResourceModel to be returned, should always have a primary Identifier. In this case RuleARN is the
        // primary Identifier.
        responseResourceModelBuilder.ruleARN(updateSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleARN());
        responseResourceModelBuilder.samplingRule(samplingRuleBuilder.build());

        return responseResourceModelBuilder.build();
    }
}