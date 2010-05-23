package katerlib;

public interface IKaterEventDispatcher {
	public void addActionListener(IKaterEventListener event);
    public void dispatchFinish();
}