package id.thork.app.helper.builder.model

import java.lang.reflect.Constructor
 class LocomotifField {
     var fieldName: String? = null
     var fieldCaption: String? = null
     var fieldValue: Any? = null
     var isMandatory: Boolean? = false

     constructor(fieldName: String?, fieldCaption: String?) {
         this.fieldName = fieldName
         this.fieldCaption = fieldCaption
     }

     constructor(fieldName: String?, fieldCaption: String?, fieldValue: Any?) {
         this.fieldName = fieldName
         this.fieldCaption = fieldCaption
         this.fieldValue = fieldValue
     }

     constructor(
         fieldName: String?,
         fieldCaption: String?,
         fieldValue: Any?,
         isMandatory: Boolean?
     ) {
         this.fieldName = fieldName
         this.fieldCaption = fieldCaption
         this.fieldValue = fieldValue
         this.isMandatory = isMandatory
     }

 }