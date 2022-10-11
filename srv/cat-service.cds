using my.bookshop as my from '../db/data-model';

service CatalogService {

  entity Books     as projection on my.Books actions {
    action customCreateNoNav(bookNo : Integer, bookType : String, title : String, stock : Integer);
    action customCreateBoundAction(bookNo : Integer, bookType : String, title : String, stock : Integer) returns Books;
    action updateComment(comment : String)                                                               returns Books;
    action retryCallRestApi()                                                                            returns Books;
    action customEditBoundAction(bookType : String, comment : String)                                    returns Books;
  };

  entity BookTypes as projection on my.BookTypes;
  action callRemoteRestApi();
  action postItems(items : array of Books);
}

annotate CatalogService.Books with @odata.draft.enabled;

annotate CatalogService.Books with @(Capabilities : {
  // entity-level
  InsertRestrictions.Insertable : false,
  UpdateRestrictions.Updatable  : false,
  DeleteRestrictions.Deletable  : canDelete
});

annotate CatalogService.Books actions {
  customCreateBoundAction
  @(
    cds.odata.bindingparameter.name : '_it',
    cds.odata.bindingparameter.collection,
    Common.SideEffects              : {TargetEntities : [_it]}
  )(bookType
  @Common.ValueList : {
    CollectionPath : 'BookTypes',
    Label          : 'Book Types',
    //SearchSupported: true
    Parameters     : [
      {
        $Type             : 'Common.ValueListParameterOut',
        LocalDataProperty : 'bookType',
        ValueListProperty : 'code'
      },
      {
        $Type             : 'Common.ValueListParameterDisplayOnly',
        ValueListProperty : 'name'
      }
    ]
  }
  );
  customCreateNoNav
  @(
    cds.odata.bindingparameter.name : '_it',
    cds.odata.bindingparameter.collection,
    Common.SideEffects              : {TargetEntities : [_it]}
  )(bookType
  @Common.ValueListWithFixedValues
  @Common.ValueList : {
    CollectionPath : 'BookTypes',
    Label          : 'Book Types',
    //SearchSupported: true
    Parameters     : [
      {
        $Type             : 'Common.ValueListParameterOut',
        LocalDataProperty : 'bookType',
        ValueListProperty : 'code'
      },
      {
        $Type             : 'Common.ValueListParameterDisplayOnly',
        ValueListProperty : 'name'
      }
    ]
  }
  );
}

annotate CatalogService.Books with @(restrict : [
  {
    grant : 'READ',
    to    : ['authenticated-user']
  },
  {
    grant : '*',
    to    : ['admin']
  }
]);
