/*
This source code file is part of the CASAA Treatment Coding System Utility
    Copyright (C) 2009  UNM CASAA

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.unm.casaa.utterance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Scanner;
import java.util.StringTokenizer;

import edu.unm.casaa.misc.MiscDataItem;

/**
 * A vector of utterances, stored in their recorded order.
 * @author Alex Manuel
 *
 */
public class UtteranceList {

	private static final long serialVersionUID 	= 1L;
	private Vector< Utterance >	list 			= new Vector< Utterance >();

	/**
	 * Append utterance.
	 * @param data
	 */
	public void	add( Utterance data ){
		list.add( data );			
	}

	/**
	 * Remove last utterance, if list is non-empty.
	 */
	public void removeLast(){
		if( !list.isEmpty() ){
			list.remove( list.size() - 1 );
		}
	}

	/**
	 * Remove utterance at given index, updating "enum" field of all later utterances.
	 */
	public void removeAt(int index){
		if( index < list.size() ){
			list.remove(index);
			for( int i = index; i < list.size(); i++ ){
				list.get(i).setEnum(i);
			}
		}
	}

	/**
	 * Return utterance at given index, or null is index is out of bounds.
	 * @param index
	 * @return the utterance at given index, or null if index is out of bounds
	 */
	public Utterance get(int index){
		if(index < list.size()){
			return list.get(index);
		}
		else{
			return null;
		}
	}

	/**
	 * Return index of last coded utterance, or -1 if none found.
	 * @return index
	 */
	public int getLastCodedUtterance(){
		// Search backwards from end of list until we find a coded element, if any.
		for( int i = list.size() - 1; i >= 0; i-- ){
			if(list.get(i).isCoded()){
				return i;
			}
		}
		return -1; // No coded utterance found.
	}

	public int size(){
		return list.size();
	}

	public boolean isEmpty(){
		return list.isEmpty();
	}

	/**
	 * Write to file.
	 * @param file
	 * @param filenameAudio
	 */
	public void writeToFile(File file, String filenameAudio){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(file, false)); //change to true if caching is implemented
			writer.println("Audio File:\t" + filenameAudio);
			for( int i = 0; i < list.size(); i++ ){
				writer.println(list.get(i).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			writer.close();
		}
	}

	/**
	 * Load from file.  Overwrites any existing contents.
	 * @param file
	 * @return filenameAudio
	 */
	public String loadFromFile(File file){
		list.clear(); // Clear existing contents.

		Scanner in = null;

		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if( !in.hasNext() ){
			return ("ERROR: No Audio File Listed");
		}
		//eat the audio filename line
		String 			filenameAudio 	= in.nextLine();
		StringTokenizer headReader 		= new StringTokenizer(filenameAudio, "\t");

		headReader.nextToken(); //eat line heading "Audio Filename:"
		filenameAudio = headReader.nextToken();
		if( (filenameAudio.trim()).equalsIgnoreCase("") ){
			return ("ERROR: No Audio File Listed");
		}
		while( in.hasNextLine() ){
			//TODO: MISC is hard-coded
			//MISC format: int order, String startTime, String EndTime,
			//				int startBytes, int endBytes,
			//				int code, String codename
			String 			nextStr 	= in.nextLine();
			StringTokenizer st 			= new StringTokenizer(nextStr/*in.nextLine()*/, "\t");
			int 			lineSize 	= st.countTokens();  //5 = parsed only, 7 = coded
			int 			order 		= new Integer(st.nextToken()).intValue();

			//TODO: place a check for "null" in start and end fields 
			//		to report to the user.
			String 			start 		= st.nextToken();
			String 			end 		= st.nextToken();
			int 			stBytes 	= new Integer(st.nextToken()).intValue();
			int 			endBytes 	= new Integer(st.nextToken()).intValue();
			MiscDataItem 	item 		= new MiscDataItem(order, start, stBytes);

			item.setEndTime(end);
			item.setEndBytes(endBytes);
			if( lineSize == 7 ){
				item.setMiscCode(new Integer(st.nextToken()).intValue());
				st.nextToken(); //throw away the code string
			}
			add(item);
		}
		return filenameAudio;
	}

}
