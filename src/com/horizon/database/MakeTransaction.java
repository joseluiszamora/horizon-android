/* Jose Luis Zamora @ Ruizmier - Mariani 2013
 * Esta clase es para gestionar de manera eficiente las nuevas transacciones, usando un objeto
 */

package com.horizon.database;

public class MakeTransaction {
	
	//private variables
	int _idTransaction;
	int _idLine;
	int _idVolume;
	String _codeProduct;
	int _quantity;
	
	// Empty constructor
	public MakeTransaction(){
	}
		
	public void start(int codetransaction) {
		if(codetransaction != 0){
			setIdTransaction(codetransaction);
			setLine(0);
			setVolume(0);
			setQuantity(0);
			setCodeProduct(null);
		}else{
			// insert error here
		}
	}
	
	
	
	
	// Set / Get values
	public void setIdTransaction(int val){
		_idTransaction = val;
	}
	
	public int getIdTransaction(){	
		return _idTransaction;
	}
	
	public void setLine(int val){
		_idLine = val;
	}
	
	public int getLine(){	
		return _idLine;
	}
	
	public void setVolume(int val){
		_idVolume = val;
	}
	
	public int getVolume(){
		return _idVolume;
	}
	
	public void setCodeProduct(String val){
		_codeProduct = val;
	}
	
	public String getCodeProduct(){
		return _codeProduct;
	}
	
	public void setQuantity(int val){
		_quantity = val;
	}
	
	public int getQuantity(){
		return _quantity;
	}
}
