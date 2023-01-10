package cloud.bangover.validation.global;

import cloud.bangover.validation.Rule;
import cloud.bangover.validation.RuleProxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class allows to perform difficult external validations, which is not fully assigned to the
 * domain logic. For example if we want to check that the value is consistent with the state of an
 * external system, we can register rule, performing that check here and then execute global rule by
 * unique alias.
 *
 * @author Dmitry Mikhaylenko
 *
 */
public class GlobalValidations {
  private static final Map<String, Rule<Object>> REGISTERED_RULES = new ConcurrentHashMap<>();

  /**
   * Register rule into registry.
   *
   * @param <T>   The under validation value type name
   * @param alias The rule alias
   * @param rule  The registered rule
   * @throws DuplicateRuleRegistrationException if rule is registered
   */
  @SuppressWarnings("unchecked")
  public static final <T> void registerRule(String alias, Rule<T> rule) {
    checkThatRuleIsNotRegistered(alias);
    REGISTERED_RULES.put(alias, (Rule<Object>) rule);
  }

  /**
   * Get rule which might be registered for the specified alias.
   *
   * @param <T>   The under validation value type name
   * @param alias The rule alias
   * @return The rule, executing original rule
   */
  public static final <T> Rule<T> getRule(String alias) {
    return new RegistryRuleProxy<>(alias);
  }

  /**
   * Clear all registration.
   */
  public static final void clear() {
    REGISTERED_RULES.clear();
  }

  private static final void checkThatRuleIsNotRegistered(String alias) {
    if (REGISTERED_RULES.containsKey(alias)) {
      throw new DuplicateRuleRegistrationException(alias);
    }
  }

  /**
   * This exception notifies about exceptional case when a rule is tried to be registered twice.
   *
   * @author Dmitry Mikhaylenko
   *
   */
  public static final class DuplicateRuleRegistrationException extends RuntimeException {
    private static final long serialVersionUID = -1455331418709482737L;

    public DuplicateRuleRegistrationException(String alias) {
      super(String.format("Rule with [%s] alias has already been registered", alias));
    }
  }

  private static class RegistryRuleProxy<T> extends RuleProxy<T> {
    @SuppressWarnings("unchecked")
    public RegistryRuleProxy(String alias) {
      super(() -> Optional.ofNullable((Rule<T>) REGISTERED_RULES.get(alias)));
    }
  }
}
