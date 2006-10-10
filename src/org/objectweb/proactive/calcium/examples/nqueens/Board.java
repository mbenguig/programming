/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive-support@inria.fr
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.calcium.examples.nqueens;

import java.util.Vector;

public class Board implements java.io.Serializable {
	
	//board dimension
	public int n;
	public int solvableSize;
		
	//solutions vector
	public long solutions[];

	// the board
	public int board[];
	
	
	public int bound1, topbit, mask; 
	
	public int row; // fila de la ultima reina fijada
	public int column; // columna de la ultima reina fijada
	public int left; // vector de diagonales hacia la izquierda
	public int down; // vector de columnas
	public int right; // vector de diagonales hacia la derecha
	
	public Board (int n, int solvableSize){
		this.n=n;
		this.solvableSize=solvableSize;
		
		this.solutions = new long[n];
		this.board = new int[n];
		
		for(int i=0;i<n;i++){
			solutions[i]=0;
		}
	}
	
	public Board(int n, int solvableSize, int row, int left, int down, int right,
			int bound1) {
		
		this(n,solvableSize);
		this.row = row;
		this.left = left;
		this.right = right;
		this.down = down;
		this.bound1 = bound1;
	}
	
	public boolean isRootBoard(){
		return true;
	}
	
	public boolean isBT1(){
		return false;
	}
	/*
	public Vector<Board> divide(){
		
		Vector<Board> v = new Vector<Board>();
		v.addAll(initDivideBT1());
		v.addAll(initDivideBT2());
		return v;
	}
	*/
}