package id.thork.app.helper.builder

class LocomotifValidator<T> constructor(val item: T) {
    private val TAG = LocomotifValidator::class.java.name

    fun <T : Any> T.getListOfValues(variableName: String): Boolean {
        return javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(Lov::class.java)) {
                    return true
                }
            }
            return false
        }
    }

    fun isListOfValues(variableName: String): Boolean? {
        val isTrue = item?.getListOfValues(variableName)
        return isTrue
    }
}