package cloud.bangover.validation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CombiningValidationService implements ValidationService {
  private final Iterable<ValidationService> validationServices;

  @Override
  public <V> ValidationState validate(V validatable) {
    ValidationState result = new ValidationState();
    for (ValidationService validationService : validationServices) {
      result = result.merge(validationService.validate(validatable));
    }
    return result;
  }
}
