package org.gaung.wiwokdetok.fondasikehidupan.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class AtLeastOneFieldNotNullValidatorTest {

    private AtLeastOneFieldNotNullValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new AtLeastOneFieldNotNullValidator();
    }

    static class DummyObject {

        private String field1;

        private String field2;

        public DummyObject(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }
    }

    @Test
    void testAllFieldsNull_ShouldReturnFalse() {
        DummyObject obj = new DummyObject(null, null);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean result = validator.isValid(obj, context);

        assertFalse(result);
    }

    @Test
    void testOneFieldNotNull_ShouldReturnTrue() {
        DummyObject obj = new DummyObject("value", null);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void testAllFieldsNotNull_ShouldReturnTrue() {
        DummyObject obj = new DummyObject("value1", "value2");
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void testNullObject_ShouldReturnFalse() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        boolean result = validator.isValid(null, context);

        assertFalse(result);
    }

    @Test
    void testFieldWithNoGetter_ShouldSkipAndReturnFalse() {
        class NoGetterField {
            private String fieldWithoutGetter;

            public NoGetterField(String fieldWithoutGetter) {
                this.fieldWithoutGetter = fieldWithoutGetter;
            }
        }

        NoGetterField obj = new NoGetterField("value1");

        boolean result = validator.isValid(obj, mock(ConstraintValidatorContext.class));
        assertFalse(result);
    }

    @Test
    void testOnlyClassProperty_ShouldReturnFalse() {
        class DummyWithOnlyClassProperty {
        }

        DummyWithOnlyClassProperty obj = new DummyWithOnlyClassProperty();

        boolean result = validator.isValid(obj, mock(ConstraintValidatorContext.class));

        assertFalse(result);
    }

    @Test
    void testGetterThrowsException_ShouldReturnFalse() {
        class ExceptionThrowing {
            public String getSomething() {
                throw new RuntimeException("boom");
            }
        }

        boolean result = validator.isValid(new ExceptionThrowing(), mock(ConstraintValidatorContext.class));

        assertFalse(result);
    }
}
