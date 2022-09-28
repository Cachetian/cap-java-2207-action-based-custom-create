package customer.cap_java_2207.handlers;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Update;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.messages.Messages;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.catalogservice.*;

import static customer.cap_java_2207.DestinationConfiguration.DEST_NAME_COVID19API;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CatalogServiceHandler.class);

  @Autowired
  Messages messages;

  @Autowired
  PersistenceService db;

  @After(event = CdsService.EVENT_READ)
  public void discountBooks(Stream<Books> books) {
    books.filter(b -> b.getTitle() != null && b.getStock() != null).filter(b -> b.getStock() > 200)
        .forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
  }

  @On(event = UpdateCommentContext.CDS_NAME)
  public void onUpdateCommentContext(UpdateCommentContext context) {
    Books entity = db.run(context.getCqn()).single(Books.class);
    entity.setComment(context.getComment());
    Result result = db.run(Update.entity(Books_.class).data(entity));
    Books record = result.single(Books.class);
    record.setIsActiveEntity(true);
    record.setHasActiveEntity(false);
    record.setHasDraftEntity(false);
    context.setResult(record);
  }

  @On(event = CustomCreateBoundActionContext.CDS_NAME, entity = Books_.CDS_NAME)
  public void onCustomCreateBoundAction(CustomCreateBoundActionContext context) {
    Books entity = Books.create();
    entity.setId(UUID.randomUUID().toString());
    entity.setBookNo(context.getBookNo());
    entity.setTitle(context.getTitle());
    entity.setStock(context.getStock());
    Result result = db.run(Insert.into(Books_.class).entry(entity));
    Books record = result.single(Books.class);
    record.setIsActiveEntity(true);
    record.setHasActiveEntity(false);
    record.setHasDraftEntity(false);
    context.setResult(record);
  }

  @On(event = CustomCreateNoNavContext.CDS_NAME, entity = Books_.CDS_NAME)
  public void onCustomCreateNoNav(CustomCreateNoNavContext context) {
    Books entity = Books.create();
    entity.setId(UUID.randomUUID().toString());
    entity.setBookNo(context.getBookNo());
    entity.setTitle(context.getTitle());
    entity.setStock(context.getStock());
    db.run(Insert.into(Books_.class).entry(entity));
    context.setCompleted();
  }

  @On(event = CallRemoteRestApiContext.CDS_NAME)
  public void onCallRemoteRestApi(CallRemoteRestApiContext context) throws IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Enter onCallRemoteRestApi context: {}", context);
    }
    HttpDestination destination = DestinationAccessor.getDestination(DEST_NAME_COVID19API).asHttp();
    HttpClient client = HttpClientAccessor.getHttpClient(destination);
    HttpResponse httpResponse = client.execute(new HttpGet("/summary"));
    String result = EntityUtils.toString(httpResponse.getEntity());
    messages.info(result);
    context.setCompleted();
  }

  @On(event = RetryCallRestApiContext.CDS_NAME)
  public void onRetryCallRestApi(RetryCallRestApiContext context) throws IOException {
    Books entity = db.run(context.getCqn()).single(Books.class);
    HttpDestination destination = DestinationAccessor.getDestination(DEST_NAME_COVID19API).asHttp();
    HttpClient client = HttpClientAccessor.getHttpClient(destination);
    HttpResponse httpResponse = client.execute(new HttpGet("/summary"));
    String comment = EntityUtils.toString(httpResponse.getEntity());
    entity.setComment(comment.substring(0, comment.length() > 32 ? 32 : comment.length()));
    Result result = db.run(Update.entity(Books_.class).data(entity));
    Books record = result.single(Books.class);
    record.setIsActiveEntity(true);
    record.setHasActiveEntity(false);
    record.setHasDraftEntity(false);
    context.setResult(record);
    context.setCompleted();
  }

}
