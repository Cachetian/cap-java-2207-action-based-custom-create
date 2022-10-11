using CatalogService as service from '../../srv/cat-service';

annotate service.Books with @(UI : {
  // CreateHidden                : false,
  DeleteHidden                : {$edmJson : {$Ne : [
    {$Path : 'showDelete'},
    true
  ]}},
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
    },
    {
      $Type  : 'UI.DataFieldForAction',
      Label  : 'Post Items',
      Action : 'CatalogService.EntityContainer/postItems',
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
  }],

  Identification              : [{
    $Type  : 'UI.DataFieldForAction', //Action in the RootEntities of the object page next to the edit button
    Action : 'CatalogService.customEditBoundAction',
    Label  : 'Custom Edit',
  }],
});
