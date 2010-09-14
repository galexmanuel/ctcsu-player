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

package edu.unm.casaa.misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import edu.unm.casaa.main.MainController;
import edu.unm.casaa.main.Style;

/**
 * This class creates the empty shell GUI for the MISC Coding interface.
 * 
 * @author UNM CASAA
 *
 */
public class MiscTemplateView extends JPanel {

	private static final long serialVersionUID = 1L;

	//====================================================================
	// Fields
	//====================================================================
	//Window Constants and Variables
	private static final int PANEL_WIDTH	= 600;
	private static final int PANEL_HEIGHT	= 450;

	//GUI Components and Constants
	private TitledBorder borderWindow		= null;
	private Dimension dimMainPanel			= null;

	private JPanel panelButtons				= null;
	private JPanel panelPrevText			= null;
	private JPanel panelCurrentText			= null;
	private JPanel panelNextText			= null;
	private JPanel panelTherapistControls	= null;
	private TitledBorder borderTherapist	= null;
	private JPanel panelClientControls		= null;
	private TitledBorder borderClient		= null;
	private static final int BUTTON_HOR_GAP	= 5;
	private static final int BUTTON_VER_GAP	= 4;

	private Dimension dimButtonSize			= null;
	private static final int BUTTON_WIDTH	= 90;
	private static final int BUTTON_HEIGHT	= 24;
	private TitledBorder borderButtons		= null;

	//User Feedback Components
	private JTextField textFieldOrder		= null;
	private static final int ORDER_COLS		= 9;
	private JLabel labelOrder				= null;
	private JTextField textFieldCode		= null;
	private static final int CODE_COLS		= 9;
	private JLabel labelCode				= null;
	private JTextField textFieldStartTime	= null;
	private JTextField textFieldEndTime		= null;
	private static final int TIME_COLS		= 20;
	private JLabel labelStart				= null;
	private JLabel labelEnd					= null;
	private TitledBorder borderCurrent		= null;

	private JTextField textFieldNext		= null;
	private static final int NEXT_COLS		= 60;
	private TitledBorder borderNext			= null;

	private JTextField textFieldPrev		= null;
	private static final int PREV_COLS		= 60;
	private TitledBorder borderPrev			= null;

	private JCheckBox checkBoxPauseUncoded	= null;

	// Coding controls.
	private HashMap< Integer, JButton > buttonMiscCode	= new HashMap< Integer, JButton >();

	//====================================================================
	// Constructor and Initialization Methods
	//====================================================================

	public MiscTemplateView(){
		init();
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private void init(){
		setBorder(getBorderWindow());
		setMaximumSize(getDimMainPanel());
		setMinimumSize(getDimMainPanel());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		parseUserControls();
		add(getPanelPrevText());
		add(getPanelCurrentText());
		add(getPanelNextText());
		add(getPanelButtons());
		setVisible(true);
	}

	//====================================================================
	// Public Getter and Setter Methods
	//====================================================================

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Misc Code Buttons
	public JButton getButtonMiscCode( MiscCode miscCode ) {
		JButton button = buttonMiscCode.get( miscCode.value );

		// Create button if it does not yet exist.
		if( button == null ) {
			button = new JButton( miscCode.label );
			button.setPreferredSize( getDimButtonSize() );
			button.setToolTipText( "" + miscCode.value );
			buttonMiscCode.put( miscCode.value, button );
		}
		return button;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public JCheckBox getCheckBoxPauseUncoded(){
		if( checkBoxPauseUncoded == null ){
			checkBoxPauseUncoded = new JCheckBox("Pause if Uncoded", true);
			/*checkBoxPauseUncoded.setToolTipText("Pauses playback if current utterance " +
			"has not been assigned a MISC code value.");*/
		}
		return checkBoxPauseUncoded;
	}

	public String toString(){
		return ("MISC");
	}

	// Parse user controls from XML file.
	private void parseUserControls() {
		String	filename 	= "userCodes.xml";
		File 	file 		= new File( filename );

		if( file.exists() ) {
			try {
				DocumentBuilderFactory 	fact 	= DocumentBuilderFactory.newInstance();
		        DocumentBuilder 		builder = fact.newDocumentBuilder();
		        Document 				doc 	= builder.parse( filename );
		        Node 					root	= doc.getDocumentElement();

		        /* Expected format:
		         * <userCodes>
		         *   <codes>
		         *    ...
		         *   </codes>
		         *   <controls panel="therapist">
		         *     ...
		         *   </controls>
		         *   <controls panel="client">
		         *     ...
		         *   </controls>
		         * </userCodes>
		         */
		        for( Node node = root.getFirstChild(); node != null; node = node.getNextSibling() ) {
			        if( node.getNodeName().equalsIgnoreCase( "controls" ) ) {
			        	// Get panel attribute.  Must be "therapist" or "client".
						NamedNodeMap 	map 		= node.getAttributes();
						String			panelName	= map.getNamedItem( "panel" ).getTextContent();

						if( panelName.equalsIgnoreCase( "therapist" ) ) {
				        	parseControlColumn( node, getPanelTherapistControls() );
						} else if( panelName.equalsIgnoreCase( "client" ) ) {
							parseControlColumn( node, getPanelClientControls() );
						}
			        }
		        }
			} catch( SAXParseException e ) {
				MainController.instance.handleUserCodesParseError( filename, e );
			} catch( Exception e ) {
				MainController.instance.handleUserCodesGenericError( filename, e );
			}
		} else {
			MainController.instance.handleUserCodesMissing( filename );
		}
	}

	//====================================================================
	// Private Helper Methods
	//====================================================================

	// Parse a column of controls from given XML node.  Add buttons to given panel, and set panel layout.
	// Each child of given node is expected to be one row of controls.
	private void parseControlColumn( Node node, JPanel panel ) {
		// Count actual rows (node children), columns (max of any node child's children),
		// for use in layout.
		int numRows = 0;
		int	maxCols	= 0;

		for( Node row = node.getFirstChild(); row != null; row = row.getNextSibling() ) {
			if( row.getNodeName().equalsIgnoreCase( "row" ) ) {
				numRows++;
			} else {
				continue;
			}

			int	colsThisRow = 0;

			for( Node cell = row.getFirstChild(); cell != null; cell = cell.getNextSibling() ) {
				if( cell.getNodeName().equalsIgnoreCase( "code" ) ||
					cell.getNodeName().equalsIgnoreCase( "group") ) {
						colsThisRow++;
				}
			}
			maxCols = Math.max( maxCols, colsThisRow );
		}

		panel.setLayout( new GridLayout( numRows, maxCols, BUTTON_HOR_GAP, BUTTON_VER_GAP ) );

		// Traverse children, creating a row of buttons for each.
		for( Node row = node.getFirstChild(); row != null; row = row.getNextSibling() ) {
			if( !row.getNodeName().equalsIgnoreCase( "row" ) ) {
				continue;
			}
			int	colsThisRow = 0;

			for( Node cell = row.getFirstChild(); cell != null; cell = cell.getNextSibling() ) {
				// If cell represents a single value, add a button assigned to that code.
				// If cell represents a group of values, add a button that will open a popup.
				if( cell.getNodeName().equalsIgnoreCase( "code" ) ) {
					NamedNodeMap 	map 	= cell.getAttributes();
					String			label	= map.getNamedItem( "label" ).getTextContent();
					MiscCode		code	= MiscCode.codeWithLabel( label );

					panel.add( getButtonMiscCode( code ) );
					colsThisRow++;
				} else if( cell.getNodeName().equalsIgnoreCase( "group" ) ) {
					// Generate a popup menu to select one code from this group.
					NamedNodeMap 	groupMap 	= cell.getAttributes();
					String			groupLabel	= groupMap.getNamedItem( "label" ).getTextContent();
					JPopupMenu 		popup 		= new JPopupMenu();

					for( Node member = cell.getFirstChild(); member != null; member = member.getNextSibling() ) {
						if( !member.getNodeName().equalsIgnoreCase( "code" ) ) {
							continue;
						}

						NamedNodeMap 	memberMap 	= member.getAttributes();
						String			memberLabel	= memberMap.getNamedItem( "label" ).getTextContent();
						JMenuItem 		item 		= new JMenuItem( memberLabel );
						MiscCode		code		= MiscCode.codeWithLabel( memberLabel );

						// Add mouse listener, keyed with code for this member in group.
						item.addMouseListener( new PopupItemListener( code ) );
						popup.add( item );
					}

					// Add button to open popup.
					JButton 		button = new JButton( groupLabel );

					button.setPreferredSize( getDimButtonSize() );
					button.setToolTipText( "Select from " + groupLabel + "group" );
					button.addMouseListener( new PopupListener( popup ) );
					panel.add( button );

					colsThisRow++;
				}
			}

			// Add spacers to match max number of cells in any row.
			for( int i = colsThisRow; i < maxCols; i++ ) {
				panelTherapistControls.add( Box.createRigidArea( getDimButtonSize() ) );
			}
		}
	}

	private JPanel getPanelTherapistControls() {
		if( panelTherapistControls == null ) {
			panelTherapistControls = new JPanel();
			panelTherapistControls.setBorder( getBorderTherapist() );
		}
		return panelTherapistControls;
	}

	private JPanel getPanelClientControls(){
		if( panelClientControls == null ){
			panelClientControls = new JPanel();
			panelClientControls.setBorder( getBorderClient() );
		}
		return panelClientControls;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private TitledBorder getBorderWindow(){
		if( borderWindow == null ){
			borderWindow = BorderFactory.createTitledBorder("MISC Coding Template");
			borderWindow.setTitleJustification(TitledBorder.CENTER);
			borderWindow.setTitleColor(Color.BLACK);
		}
		return borderWindow;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JPanel getPanelButtons(){
		if( panelButtons == null ){
			panelButtons = new JPanel();
			panelButtons.setBorder(getBorderButtons());
			panelButtons.setLayout(new FlowLayout());

			JPanel panelLeft = new JPanel();

			panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
			panelLeft.add(getPanelTherapistControls());
			panelLeft.add(getCheckBoxPauseUncoded());
			panelButtons.add(panelLeft);
			panelButtons.add(getPanelClientControls());
		}
		return panelButtons;
	}

	private TitledBorder getBorderButtons(){
		if( borderButtons == null ){
			borderButtons = BorderFactory.createTitledBorder("MISC Coding Controls");
			borderButtons.setTitleJustification(TitledBorder.LEADING);
		}
		return borderButtons;
	}

	private Dimension getDimButtonSize(){
		if( dimButtonSize == null ){
			dimButtonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
		}
		return dimButtonSize;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldOrder(){
		if( textFieldOrder == null ){
			textFieldOrder = new JTextField(ORDER_COLS);
			textFieldOrder.setEditable(false);
			Style.configureStrongText( textFieldOrder );
		}
		return textFieldOrder;
	}

	public void setTextFieldOrder(String text){
		getTextFieldOrder().setText(text);
	}

	private JLabel getLabelOrder(){
		if( labelOrder == null ){
			labelOrder = new JLabel("Enumeration");
			labelOrder.setLabelFor(getTextFieldOrder());
		}
		return labelOrder;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldCode(){
		if( textFieldCode == null ){
			textFieldCode = new JTextField(CODE_COLS);
			textFieldCode.setEditable(false);
			Style.configureStrongText( textFieldCode );
		}
		return textFieldCode;
	}

	public void setTextFieldCode(String utteranceString){
		getTextFieldCode().setText(utteranceString);
	}

	private JLabel getLabelCode(){
		if( labelCode == null ){
			labelCode = new JLabel("MISC Code");
			labelCode.setLabelFor(getTextFieldOrder());
		}
		return labelCode;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldStartTime(){
		if( textFieldStartTime == null ){
			textFieldStartTime = new JTextField(TIME_COLS);
			textFieldStartTime.setEditable(false);
			Style.configureStrongText( textFieldStartTime );
		}
		return textFieldStartTime;
	}

	public void setTextFieldStartTime(String utteranceString){
		getTextFieldStartTime().setText(utteranceString);
	}

	public void setTextFieldStartTimeColor(Color color){
		getTextFieldStartTime().setForeground(color);
	}

	private JLabel getLabelStart(){
		if( labelStart == null ){
			labelStart = new JLabel("Start Time");
			labelStart.setLabelFor(getTextFieldStartTime());
		}
		return labelStart;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldEndTime(){
		if( textFieldEndTime == null ){
			textFieldEndTime = new JTextField(TIME_COLS);
			textFieldEndTime.setEditable(false);
			Style.configureStrongText( textFieldEndTime );
		}
		return textFieldEndTime;
	}

	public void setTextFieldEndTime(String utteranceString){
		getTextFieldEndTime().setText(utteranceString);
	}

	private JLabel getLabelEnd(){
		if( labelEnd == null ){
			labelEnd = new JLabel("End Time");
			labelEnd.setLabelFor(getTextFieldEndTime());
		}
		return labelEnd;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldNext(){
		if( textFieldNext == null ){
			textFieldNext = new JTextField(NEXT_COLS);
			textFieldNext.setEditable(false);
			Style.configureLightText( textFieldNext );
		}
		return textFieldNext;
	}

	public void setTextFieldNext(String utteranceString){
		getTextFieldNext().setText(utteranceString);
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JTextField getTextFieldPrev(){
		if( textFieldPrev == null ){
			textFieldPrev = new JTextField(PREV_COLS);
			textFieldPrev.setEditable(false);
			Style.configureLightText( textFieldPrev );
		}
		return textFieldPrev;
	}

	public void setTextFieldPrev(String utteranceString){
		getTextFieldPrev().setText(utteranceString);
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JPanel getPanelCurrentText(){
		if( panelCurrentText == null ){
			panelCurrentText = new JPanel();
			panelCurrentText.setBorder(getBorderCurrent());
			panelCurrentText.setLayout(new GridBagLayout());

			GridBagConstraints orderC = new GridBagConstraints();
			orderC.gridx = 0;
			orderC.gridy = 0;
			orderC.weightx = 1.0;
			orderC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getLabelOrder(), orderC);

			GridBagConstraints codeC = new GridBagConstraints();
			codeC.gridx = 1;
			codeC.gridy = 0;
			codeC.weightx = 1.0;
			codeC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getLabelCode(), codeC);

			GridBagConstraints startC = new GridBagConstraints();
			startC.gridx = 2;
			startC.gridy = 0;
			startC.weightx = 1.0;
			startC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getLabelStart(), startC);

			GridBagConstraints endC = new GridBagConstraints();
			endC.gridx = 3;
			endC.gridy = 0;
			endC.weightx = 1.0;
			endC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getLabelEnd(), endC);

			GridBagConstraints orderTC = new GridBagConstraints();
			orderTC.gridx = 0;
			orderTC.gridy = 1;
			orderTC.weightx = 1.0;
			orderTC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getTextFieldOrder(), orderTC);

			GridBagConstraints codeTC = new GridBagConstraints();
			codeTC.gridx = 1;
			codeTC.gridy = 1;
			codeTC.weightx = 1.0;
			codeTC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getTextFieldCode(), codeTC);

			GridBagConstraints startTC = new GridBagConstraints();
			startTC.gridx = 2;
			startTC.gridy = 1;
			startTC.weightx = 1.0;
			startTC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getTextFieldStartTime(), startTC);

			GridBagConstraints endTC = new GridBagConstraints();
			endTC.gridx = 3;
			endTC.gridy = 1;
			endTC.weightx = 1.0;
			endTC.anchor = GridBagConstraints.LINE_START;
			panelCurrentText.add(getTextFieldEndTime(), endTC);
		}
		return panelCurrentText;
	}

	private TitledBorder getBorderCurrent(){
		if( borderCurrent == null ){
			borderCurrent = BorderFactory.createTitledBorder("Current Utterance");
			borderCurrent.setTitleJustification(TitledBorder.LEADING);
		}
		return borderCurrent;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JPanel getPanelPrevText(){
		if( panelPrevText == null ){
			panelPrevText = new JPanel();
			panelPrevText.setBorder(getBorderPrev());
			panelPrevText.setLayout(new BorderLayout());
			panelPrevText.add(getTextFieldPrev(), BorderLayout.CENTER);
		}
		return panelPrevText;
	}

	private TitledBorder getBorderPrev(){
		if( borderPrev == null ){
			borderPrev = BorderFactory.createTitledBorder("Previous Utterance");
			borderPrev.setTitleJustification(TitledBorder.LEADING);
		}
		return borderPrev;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private JPanel getPanelNextText(){
		if( panelNextText == null ){
			panelNextText = new JPanel();
			panelNextText.setBorder(getBorderNext());
			panelNextText.setLayout(new BorderLayout());
			panelNextText.add(getTextFieldNext(), BorderLayout.CENTER);
		}
		return panelNextText;
	}

	private TitledBorder getBorderNext(){
		if( borderNext == null ){
			borderNext = BorderFactory.createTitledBorder("Next Utterance");
			borderNext.setTitleJustification(TitledBorder.LEADING);
		}
		return borderNext;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private TitledBorder getBorderTherapist(){
		if( borderTherapist == null ){
			borderTherapist = BorderFactory.createTitledBorder(
			"Therapist Codes");
			borderTherapist.setTitleJustification(TitledBorder.LEADING);
		}
		return borderTherapist;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private TitledBorder getBorderClient(){
		if( borderClient == null ){
			borderClient = BorderFactory.createTitledBorder("Client Codes");
			borderClient.setTitleJustification(TitledBorder.LEADING);
		}
		return borderClient;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private Dimension getDimMainPanel(){
		if( dimMainPanel == null ){
			dimMainPanel = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
		}
		return dimMainPanel;
	}

	//===============================================================
	// PopupListener
	//===============================================================

	private class PopupListener extends MouseAdapter {
		private JPopupMenu popup = null;

		public PopupListener( JPopupMenu popup ) {
			this.popup = popup;
		}

		public void mousePressed( MouseEvent e ) {
            popup.show( e.getComponent(), e.getX(), e.getY() );
	    }
	}

	//===============================================================
	// PopupItemListener
	//===============================================================

	private class PopupItemListener extends MouseAdapter {
		private MiscCode code;
		
		PopupItemListener( MiscCode code ) {
			this.code = code;
		}
		
		public void mousePressed( MouseEvent e ) {
			MainController.instance.handleButtonMiscCode( code );
		}
				
	}
}
