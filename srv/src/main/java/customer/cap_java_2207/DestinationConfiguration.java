package customer.cap_java_2207;

import com.sap.cds.services.application.ApplicationLifecycleService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.springframework.stereotype.Component;

@Component
@ServiceName(ApplicationLifecycleService.DEFAULT_NAME)
public class DestinationConfiguration implements EventHandler {
  public static String DEST_NAME_COVID19API = "covid19api";

  @Before(event = ApplicationLifecycleService.EVENT_APPLICATION_PREPARED)
  public void initializeDestinations() {
    DefaultHttpDestination httpDestination = DefaultHttpDestination
        .builder("https://api.covid19api.com")
        .name(DEST_NAME_COVID19API).build();

    DestinationAccessor.appendDestinationLoader(
        new DefaultDestinationLoader().registerDestination(httpDestination));

  }
}
