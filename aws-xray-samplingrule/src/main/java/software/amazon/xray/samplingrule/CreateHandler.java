package software.amazon.xray.samplingrule;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.CreateSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.CreateSamplingRuleResponse;
import software.amazon.awssdk.services.xray.model.Tag;
import software.amazon.awssdk.services.xray.model.InvalidRequestException;
import software.amazon.awssdk.services.xray.model.SamplingRule;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.xray.samplingrule.SamplingRule.SamplingRuleBuilder;
import software.amazon.xray.samplingrule.SamplingRuleRecord.SamplingRuleRecordBuilder;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateHandler extends BaseHandler<CallbackContext> {

    private static final String RESOURCE_ALREADY_EXISTS_ERR_MSG = "EXISTS";
    private XRayClient client;

    public CreateHandler() {
        this.client = ClientBuilder.getClient();
    }

    @VisibleForTesting
    public CreateHandler(XRayClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel requestResourceModel = request.getDesiredResourceState();

        ResourceModel responseResourceModel = doCreateSamplingRule(proxy, requestResourceModel, client);

        logger.log(String.format("%s [%s] created successfully",
                ResourceModel.TYPE_NAME, responseResourceModel.getPrimaryIdentifier().toString()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(responseResourceModel)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private ResourceModel doCreateSamplingRule(AmazonWebServicesClientProxy proxy, ResourceModel model,
                                               XRayClient client) {
        CreateSamplingRuleRequest createSamplingRuleRequest = buildCreateSamplingRuleRequest(model);
        CreateSamplingRuleResponse createSamplingRuleResponse;
        try {
            createSamplingRuleResponse = proxy.injectCredentialsAndInvokeV2(createSamplingRuleRequest,
                    client::createSamplingRule);
        } catch (InvalidRequestException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains(RESOURCE_ALREADY_EXISTS_ERR_MSG)) {
                throw new CfnAlreadyExistsException(e);
            }
            throw new CfnInvalidRequestException(createSamplingRuleRequest.toString(), e);
        }

        return buildResponseResourceModel(createSamplingRuleResponse);
    }

    private CreateSamplingRuleRequest buildCreateSamplingRuleRequest(ResourceModel model) {
        List<Tag> tags = new ArrayList<>();
        SamplingRule.Builder samplingRuleBuilder = SamplingRule.builder();
        CreateSamplingRuleRequest.Builder createSamplingRuleRequestBuilder = CreateSamplingRuleRequest.builder();

        samplingRuleBuilder.attributes(model.getSamplingRule().getAttributes());
        samplingRuleBuilder.fixedRate(model.getSamplingRule().getFixedRate());
        samplingRuleBuilder.host(model.getSamplingRule().getHost());
        samplingRuleBuilder.httpMethod(model.getSamplingRule().getHTTPMethod());
        samplingRuleBuilder.priority(model.getSamplingRule().getPriority());
        samplingRuleBuilder.reservoirSize(model.getSamplingRule().getReservoirSize());
        samplingRuleBuilder.resourceARN(model.getSamplingRule().getResourceARN());
        samplingRuleBuilder.ruleARN(model.getSamplingRule().getRuleARN());
        samplingRuleBuilder.ruleName(model.getSamplingRule().getRuleName());
        samplingRuleBuilder.serviceName(model.getSamplingRule().getServiceName());
        samplingRuleBuilder.serviceType(model.getSamplingRule().getServiceType());
        samplingRuleBuilder.urlPath(model.getSamplingRule().getURLPath());
        samplingRuleBuilder.version(model.getSamplingRule().getVersion());
        createSamplingRuleRequestBuilder.samplingRule(samplingRuleBuilder.build());

        if (model.getTags() != null) {
            for (Tags eachTag : model.getTags()) {
                Tag tag = Tag.builder().key(eachTag.getKey())
                        .value(eachTag.getValue())
                        .build();
                tags.add(tag);
            }
            createSamplingRuleRequestBuilder.tags(tags);
        }

        return createSamplingRuleRequestBuilder.build();
    }

    private ResourceModel buildResponseResourceModel(CreateSamplingRuleResponse createSamplingRuleResponse) {
        SamplingRuleBuilder samplingRuleBuilder = software.amazon.xray.samplingrule.SamplingRule.builder();
        ResourceModel.ResourceModelBuilder responseResourceModelBuilder = ResourceModel.builder();

        samplingRuleBuilder.attributes(createSamplingRuleResponse.samplingRuleRecord().samplingRule().attributes());
        samplingRuleBuilder.fixedRate(createSamplingRuleResponse.samplingRuleRecord().samplingRule().fixedRate());
        samplingRuleBuilder.host(createSamplingRuleResponse.samplingRuleRecord().samplingRule().host());
        samplingRuleBuilder.hTTPMethod(createSamplingRuleResponse.samplingRuleRecord().samplingRule().httpMethod());
        samplingRuleBuilder.priority(createSamplingRuleResponse.samplingRuleRecord().samplingRule().priority());
        samplingRuleBuilder.resourceARN(createSamplingRuleResponse.samplingRuleRecord().samplingRule().resourceARN());
        samplingRuleBuilder.reservoirSize(createSamplingRuleResponse.samplingRuleRecord().samplingRule().reservoirSize());
        samplingRuleBuilder.ruleARN(createSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleARN());
        samplingRuleBuilder.ruleName(createSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleName());
        samplingRuleBuilder.serviceName(createSamplingRuleResponse.samplingRuleRecord().samplingRule().serviceName());
        samplingRuleBuilder.serviceType(createSamplingRuleResponse.samplingRuleRecord().samplingRule().serviceType());
        samplingRuleBuilder.uRLPath(createSamplingRuleResponse.samplingRuleRecord().samplingRule().urlPath());
        samplingRuleBuilder.version(createSamplingRuleResponse.samplingRuleRecord().samplingRule().version());

        // ResourceModel to be returned, should always have a primary Identifier. In this case RuleARN is the
        // primary Identifier.
        responseResourceModelBuilder.ruleARN(createSamplingRuleResponse.samplingRuleRecord().samplingRule().ruleARN());
        responseResourceModelBuilder.samplingRule(samplingRuleBuilder.build());

        return responseResourceModelBuilder.build();
    }
}