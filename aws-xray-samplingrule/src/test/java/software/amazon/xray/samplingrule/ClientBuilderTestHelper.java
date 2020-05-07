package software.amazon.xray.samplingrule;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilderTestHelper {

    private static Region region = Region.of("us-west-2");

    public static XRayClient getClient() {
        return XRayClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .region(region)
                .build();
    }
}