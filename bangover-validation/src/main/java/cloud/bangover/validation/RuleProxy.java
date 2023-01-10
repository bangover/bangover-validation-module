package cloud.bangover.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * This class allows wrap original rule to the proxy-object and redefine original behavior if it is
 * needed.
 *
 * @param <T> The rule validated value type name
 */
@RequiredArgsConstructor
public class RuleProxy<T> implements Rule<T> {
  private final ProxiedRuleProvider<T> ruleProvider;

  @Override
  public boolean isAcceptableFor(T value) {
    return getOriginalRule().map(rule -> rule.isAcceptableFor(value)).orElse(false);
  }

  @Override
  public Collection<ErrorMessage> check(T value) {
    return getOriginalRule().map(rule -> rule.check(value)).orElse(Collections.emptyList());
  }

  @Override
  public Rule<T> invert() {
    return new RuleProxy<>(() -> getOriginalRule().map(Rule::invert));
  }

  private Optional<Rule<T>> getOriginalRule() {
    return ruleProvider.getProxiedRule();
  }

  /**
   * This interface provides the original rule that is proxied.
   *
   * @param <T> The rule validated value type name
   */
  public interface ProxiedRuleProvider<T> {
    Optional<Rule<T>> getProxiedRule();
  }
}
