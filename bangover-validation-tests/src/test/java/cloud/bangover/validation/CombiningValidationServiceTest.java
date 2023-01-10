package cloud.bangover.validation;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CombiningValidationServiceTest {
  private static final ErrorMessage ERROR_MESSAGE_1 = ErrorMessage.createFor("Error 1");
  private static final ErrorMessage ERROR_MESSAGE_2 = ErrorMessage.createFor("Error 2");
  private static final ValidationState VALIDATION_STATE_1 =
      new ValidationState().withUngrouped(ERROR_MESSAGE_1);
  private static final ValidationState VALIDATION_STATE_2 =
      new ValidationState().withUngrouped(ERROR_MESSAGE_2);
  private static final ValidationState VALIDATION_STATE_3 =
      VALIDATION_STATE_1.merge(VALIDATION_STATE_2);

  @Test
  public void shouldCombineAllValidationStates() {
    // Given
    ValidationService firstService = new StubValidationService(VALIDATION_STATE_1);
    ValidationService secondService = new StubValidationService(VALIDATION_STATE_2);
    ValidationService combinedService =
        new CombiningValidationService(Arrays.asList(firstService, secondService));
    // When
    ValidationState resultState = combinedService.validate(new Object());
    // Then
    Assert.assertEquals(VALIDATION_STATE_3, resultState);
  }
}
