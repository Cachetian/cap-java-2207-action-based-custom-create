package customer.cap_java_2207.handlers;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
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

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
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

  @Before(event = CdsService.EVENT_READ, entity = Books_.CDS_NAME)
  public void beforeReadBooks(CdsReadEventContext context) {
    // modify where condition
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Enter -> beforeReadBooks context: {}, cqn: {}", context, context.getCqn());
    }
    String cqnQuery = context.getCqn().toString();
    context.setCqn(Select.cqn(cqnQuery).where(CQL.get("title").startsWith("A")));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("beforeReadBooks -> Change context cqn to: {}", context.getCqn());
    }
  }

  @After(event = CdsService.EVENT_READ)
  public void discountBooks(Stream<Books> books) {
    books.filter(b -> b.getTitle() != null && b.getStock() != null).filter(b -> b.getStock() > 200)
        .forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
  }

  @After(event = CdsService.EVENT_READ)
  public void processShowDeleteButton(Stream<Books> books) {
    books.forEach(b -> {
      String commet = b.getComment();
      if (commet != null) {
        b.setShowDelete(commet.contains("showDelete"));
        b.setCanDelete(commet.contains("canDelete"));
      } else {
        b.setShowDelete(false);
        b.setCanDelete(false);
      }
    });
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
    entity.setBookTypeCode(context.getBookType());
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
    entity.setBookTypeCode(context.getBookType());
    entity.setPublishDateTime(Instant.now());
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

  @On(event = PostItemsContext.CDS_NAME)
  public void onPostItems(PostItemsContext context) {
    Collection<Books> books = context.getItems();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Enter onPostItems books.size: {}", books.size());
    }
    // https://cap.cloud.sap/docs/java/changeset-contexts#defining-changeset-contexts
    for (Books book : books) {
      context.getCdsRuntime().changeSetContext().run(ctx -> {
        // executes inside a dedicated ChangeSet Context
        db.run(Insert.into(Books_.class).entry(book));
      });
      messages.info("dummy info level message " + book.getTitle());
      messages.success("dummy succ level message " + book.getBookNo());
    }
    messages.error("dummy exception");
    messages.throwIfError();
    context.setCompleted();
  }

  @On(event = PostNewBooksContext.CDS_NAME)
  public void onPostNewBooks(PostNewBooksContext context) {
    Collection<String> titles = context.getTitles();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Enter onPostNewBooks titles.size: {}", titles.size());
    }
    // https://cap.cloud.sap/docs/java/changeset-contexts#defining-changeset-contexts
    for (String title : titles) {
      Books book = Books.create();
      book.setId(UUID.randomUUID().toString());
      book.setBookNo(0);
      book.setTitle(title);
      context.getCdsRuntime().changeSetContext().run(ctx -> {
        // executes inside a dedicated ChangeSet Context
        db.run(Insert.into(Books_.class).entry(book));
      });
      messages.info("dummy info level message " + book.getTitle());
      messages.success("dummy succ level message " + book.getBookNo());
      if (title.contains("error")) {
        messages.error("dummy exception");
      }
    }

    messages.throwIfError();
    context.setCompleted();
  }

  @On(event = CustomEditBoundActionContext.CDS_NAME)
  public void onCustomEditBoundAction(CustomEditBoundActionContext context) {
    Books entity = db.run(context.getCqn()).single(Books.class);
    entity.setComment(context.getComment());
    entity.setBookTypeCode((context.getBookType()));
    entity.setComment(context.getComment());
    Result result = db.run(Update.entity(Books_.class).data(entity));
    Books record = result.single(Books.class);
    record.setIsActiveEntity(true);
    record.setHasActiveEntity(false);
    record.setHasDraftEntity(false);
    context.setResult(record);
  }

}
