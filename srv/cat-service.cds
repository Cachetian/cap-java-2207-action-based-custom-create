using my.bookshop as my from '../db/data-model';

service CatalogService {

  entity Books as projection on my.Books actions {
    action customCreateNoNav(bookNo : Integer, title : String, stock : Integer);
    action customCreateBoundAction(bookNo : Integer, title : String, stock : Integer) returns Books;
    action updateComment(comment : String)                                            returns Books;
    action retryCallRestApi()                                                         returns Books;
  };

  action callRemoteRestApi();

}

annotate service.Books with @odata.draft.enabled;

annotate service.Books actions {
  customCreateBoundAction
  @(
    cds.odata.bindingparameter.name : '_it',
    cds.odata.bindingparameter.collection,
    Common.SideEffects              : {TargetEntities : [_it]}
  );
  customCreateNoNav
  @(
    cds.odata.bindingparameter.name : '_it',
    cds.odata.bindingparameter.collection,
    Common.SideEffects              : {TargetEntities : [_it]}
  )
}
