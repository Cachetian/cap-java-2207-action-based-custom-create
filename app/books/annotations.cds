using CatalogService as service from '../../srv/cat-service';

annotate service.Books with @odata.draft.enabled;

annotate service.Books with @(UI : {
  CreateHidden                : false,
  DeleteHidden                : true,
  UpdateHidden                : true,
  LineItem                    : [
    {
      $Type : 'UI.DataField',
      Label : 'No',
      Value : bookNo,
    },
    {
      $Type : 'UI.DataField',
      Label : 'title',
      Value : title,
    },
    {
      $Type : 'UI.DataField',
      Label : 'stock',
      Value : stock,
    },
    {
      $Type : 'UI.DataField',
      Label : 'comment',
      Value : comment,
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Custom Create',
      Action : 'CatalogService.customCreateBoundAction',
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Quick Create',
      Action : 'CatalogService.customCreateNoNav',
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Update Comment',
      Action : 'CatalogService.updateComment',
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Retry',
      Action : 'CatalogService.retryCallRestApi',
      Inline : true,
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Call API',
      Action : 'CatalogService.EntityContainer/callRemoteRestApi',
    }
  ],

  FieldGroup #GeneratedGroup1 : {
    $Type : 'UI.FieldGroupType',
    Data  : [
      {
        $Type : 'UI.DataField',
        Label : 'ID',
        Value : ID,
      },
      {
        $Type : 'UI.DataField',
        Label : 'No',
        Value : bookNo,
      },
      {
        $Type : 'UI.DataField',
        Label : 'title',
        Value : title,
      },
      {
        $Type : 'UI.DataField',
        Label : 'stock',
        Value : stock,
      },
      {
        $Type : 'UI.DataField',
        Label : 'comment',
        Value : comment,
      }
    ],
  },
  Facets                      : [{
    $Type  : 'UI.ReferenceFacet',
    ID     : 'GeneratedFacet1',
    Label  : 'General Information',
    Target : '@UI.FieldGroup#GeneratedGroup1',
  }]
});

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
