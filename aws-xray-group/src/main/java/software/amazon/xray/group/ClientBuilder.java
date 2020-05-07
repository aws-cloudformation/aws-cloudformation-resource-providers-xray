package software.amazon.xray.group;

import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    private final static XRayClient client = XRayClient.builder().httpClient(LambdaWrapper.HTTP_CLIENT).build();
    static XRayClient getClient() {
        return client;
    }
}
