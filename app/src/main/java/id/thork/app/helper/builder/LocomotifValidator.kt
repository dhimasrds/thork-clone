package id.thork.app.helper.builder

class LocomotifValidator<T> constructor(val item: T) {
    private val TAG = LocomotifValidator::class.java.name

    /**
     * LOV Validator
     */
    fun <T : Any> T.getListOfValues(variableName: String): Boolean {
        javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(LocoLov::class.java)) {
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

    /**
     * Lov Extension Validator
     */
    fun <T : Any> T.getListOfValuesExtension(variableName: String): Boolean {
        javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(LocoLovExtension::class.java)) {
                    return true
                }
            }
            return false
        }
    }

    fun isListOfValuesExtension(variableName: String): Boolean? {
        val isTrue = item?.getListOfValuesExtension(variableName)
        return isTrue
    }

    /**
     * Radio Button Validator
     */
    fun <T : Any> T.getRadioButton(variableName: String): Boolean {
        javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(LocoRadioButton::class.java)) {
                    return true
                }
            }
            return false
        }
    }

    fun isRadioButton(variableName: String): Boolean? {
        val isTrue = item?.getRadioButton(variableName)
        return isTrue
    }

    /**
     * CheckBox Validator
     */
    fun <T : Any> T.getCheckBox(variableName: String): Boolean {
        javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(LocoCheckBox::class.java)) {
                    return true
                }
            }
            return false
        }
    }

    fun isCheckBox(variableName: String): Boolean? {
        val isTrue = item?.getCheckBox(variableName)
        return isTrue
    }

    /**
     * Spinner Validator
     */
    fun <T : Any> T.getSpinner(variableName: String): Boolean {
        javaClass.getDeclaredField(variableName).let { field ->
            field.isAccessible = true
            for (annotation in field.annotations) {
                if (field.isAnnotationPresent(LocoSpinner::class.java)) {
                    return true
                }
            }
            return false
        }
    }

    fun isSpinner(variableName: String): Boolean? {
        val isTrue = item?.getSpinner(variableName)
        return isTrue
    }
}