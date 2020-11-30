package id.thork.app.base

class BaseParam {
    companion object {
        const val TRUE = 1
        const val FALSE = 0

        const val EMPTY_STRING = ""
        const val DASH = "-"

        const val DEFAULT_LANG = "eng"
        const val DEFAULT_LANG_DETAIL = "English"
        const val IND_LANG = "ind"
        const val IND_LANG_DETAIL = "Indonesia"

        /**
         * Authentication parameter
         */
        const val MAX_AUTH = "MAXAUTH"
        const val AUTHORIZATION = "AUTHORIZATION"
        const val X_METHOD_OVERRIDE = "x-method-override"
        const val PATCH = "PATCH"
        const val MERGE = "MERGE"
        const val SLUG = "Slug"
        const val X_DOCUMENT_DESCRIPTION = "x-document-description"
        const val X_DOCUMENT_META = "x-document-meta"
        const val CONTENT_TYPE = "Content-Type"
        const val PROPERTIES = "properties"
        const val ALL_PROPERTIES = "*"
    }
}