package net.sf.timeslottracker.data.xml;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Data save action
 *
 * @author glazachev
 */
public class DataSaveAction extends Action {

  public static final String ACTION_NAME = "Data save action";

  /**
	 * Create success action
	 */
	public DataSaveAction(Object source) {
		this(source, null);
	}

	/**
	 * Create error action
	 * 
	 * @param errorMessage
	 *            error message
	 */
	public DataSaveAction(Object source, String errorMessage) {
		super(ACTION_NAME, source, errorMessage);
	}

	/**
	 * @return error flag: true - error exists, false - no error
	 */
	public boolean hasError() {
		return !StringUtils.isBlank(getErrorMessage());
	}

	/**
	 * @return error message
	 */
	public String getErrorMessage() {
		return (String) getParam();
	}

}
