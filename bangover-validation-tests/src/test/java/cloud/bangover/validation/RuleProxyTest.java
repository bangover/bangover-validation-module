package cloud.bangover.validation;

import cloud.bangover.validation.RuleExecutor.RuleExecutionReport;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RuleProxyTest {
  private final Object VALIDATABLE_OBJECT = new Object();
  private final StubRule<Object> RULE = new StubRule<Object>(Object.class)
      .stubFor(VALIDATABLE_OBJECT, true, Arrays.asList());
  private final Rule<Object> PROXY_RULE = new RuleProxy<>(() -> Optional.of(RULE));
  private final Rule<Object> UNKNOWN_PROXY_RULE = new RuleProxy<>(() -> Optional.empty());
  
  @Test
  public void shouldUnknownRuleBeSkipped() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(VALIDATABLE_OBJECT, UNKNOWN_PROXY_RULE);
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertFalse(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
  }
  
  @Test
  public void shouldRuleBePassed() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(VALIDATABLE_OBJECT, PROXY_RULE);
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertTrue(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
    Assert.assertTrue(report.contains(Arrays.asList()));
  }
  
  @Test
  public void shouldInvertedRuleBePassedForNotValidValue() {
    // Given
    RuleExecutor<Object> ruleExecutor = RuleExecutor.of(VALIDATABLE_OBJECT, PROXY_RULE.invert());
    // When
    RuleExecutionReport report = ruleExecutor.execute();
    // Then
    Assert.assertTrue(report.isAcceptable());
    Assert.assertTrue(report.ruleIsPassed());
    Assert.assertTrue(report.contains(Arrays.asList()));       
  }
}
