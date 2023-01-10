package cloud.bangover.validation;

import cloud.bangover.validation.ValidationExecutor.ValidationReport;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.ToString.Include;

/**
 * This class is the base validation case check executor.
 *
 * @author Dmitry Mikhaylenko
 *
 */
@ToString(onlyExplicitlyIncluded = true)
public abstract class ValidationCase {
  @Getter
  @Include
  private final Object validatableObject;
  @Getter
  @Include
  private final ExpectedResult expectedRuleResult;
  @Getter
  @Include
  private final Collection<String> expectedErrorMessages;

  private final Collection<FixtureHandler> beforeHandlers = new ArrayList<>();
  private final Collection<FixtureHandler> afterHandlers = new ArrayList<>();

  /**
   * This method append handler which is executed before validation scenario.
   * 
   * @param beforeMethod The before handler
   * @return The validation case
   */
  public ValidationCase withBefore(FixtureHandler beforeMethod) {
    this.beforeHandlers.add(beforeMethod);
    return this;
  }

  /**
   * This method append handler which is executed after validation scenario.
   * 
   * @param afterMethod The before handler
   * @return The validation case
   */
  public ValidationCase withAfter(FixtureHandler afterMethod) {
    this.afterHandlers.add(afterMethod);
    return this;
  }

  /**
   * Create validation case.
   *
   * @param validatable      The validatable value
   * @param expectedResult   The expected result type(PASSED or FAILED)
   * @param expectedMessages The expected error messages
   */
  public ValidationCase(Object validatable, ExpectedResult expectedResult,
      Collection<String> expectedMessages) {
    super();
    this.validatableObject = validatable;
    this.expectedErrorMessages = expectedMessages;
    this.expectedRuleResult = expectedResult;
  }

  /**
   * Execute validation case.
   *
   * @param validationService The validation service which validates object
   * @return The validation case report
   */
  public ValidationCaseReport execute(ValidationService validationService) {
    executeBeforeHandlers();
    ValidationCaseReport result = executeValidation(validationService);
    executeAfterHandlers();
    return result;
  }

  private void executeBeforeHandlers() {
    executeFixtureHandlers(this.beforeHandlers);
  }

  private void executeAfterHandlers() {
    executeFixtureHandlers(this.afterHandlers);
  }

  private void executeFixtureHandlers(Collection<FixtureHandler> handlers) {
    handlers.forEach(FixtureHandler::execute);
  }

  private ValidationCaseReport executeValidation(ValidationService validationService) {
    ValidationExecutor validationExecutor =
        new ValidationExecutor(this.validatableObject, validationService);
    ValidationReport report = validationExecutor.execute();
    return new ValidationCaseReport() {
      @Override
      public boolean isPassed() {
        return report.isPassed() == shouldBePassed();
      }

      @Override
      public boolean containsExpectedErrorMessages() {
        return ValidationCase.this.containsExpectedErrorMessages(report);
      }
    };
  }

  private boolean shouldBePassed() {
    return expectedRuleResult.shouldBeValid;
  }

  protected abstract boolean containsExpectedErrorMessages(ValidationReport validationReport);

  /**
   * The validation case report.
   *
   * @author Dmitry Mikhaylenko
   *
   */
  public interface ValidationCaseReport {
    /**
     * Check that the validation case is passed.
     *
     * @return True if passed and false otherwise
     */
    public boolean isPassed();

    /**
     * Check that all requested error messages are contained.
     *
     * @return True if contained and false otherwise
     */
    public boolean containsExpectedErrorMessages();
  }

  /**
   * This class represents expected result type.
   *
   * @author Dmiry Mikhaylenko
   *
   */
  @AllArgsConstructor
  public enum ExpectedResult {
    PASSED(true),
    FAILED(false);

    private boolean shouldBeValid;
  }

  /**
   * This interface describes the fixture method handler signature which is executed before or after
   * validation testing scenario.
   *
   * @author Dmitry Mikhaylenko
   */
  public interface FixtureHandler {
    /**
     * Execute handler.
     */
    void execute();
  }
}
