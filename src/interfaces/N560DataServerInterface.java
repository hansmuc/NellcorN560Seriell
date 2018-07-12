package interfaces;

import data.N560Data;
import rules.NoDataException;

public interface N560DataServerInterface {
	public   N560Data getN560Data() throws NoDataException;
	 public   void start() ;
}
