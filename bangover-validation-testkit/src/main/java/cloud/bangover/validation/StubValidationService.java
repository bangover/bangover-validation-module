package cloud.bangover.validation;

import cloud.bangover.StubbingQueue;

public class StubValidationService implements ValidationService {
  private final StubbingQueue<ValidationState> stubbingQueue;

  public StubValidationService() {
    this(new ValidationState());
  }

  public StubValidationService(ValidationState defaultState) {
    this.stubbingQueue = new StubbingQueue<>(defaultState);
  }

  public StubbingQueue.StubbingQueueConfigurer<ValidationState> configure() {
    return stubbingQueue.configure();
  }

  @Override
  public <V> ValidationState validate(V validatable) {
    return stubbingQueue.peek();
  }
}
