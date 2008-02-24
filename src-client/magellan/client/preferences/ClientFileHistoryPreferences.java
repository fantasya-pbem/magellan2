// class magellan.client.preferences.ClientFileHistoryPreferences
// created on 15.02.2008
//
// Copyright 2003-2008 by magellan project team
//
// Author : $Author: $
// $Id: $
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program (see doc/LICENCE.txt); if not, write to the
// Free Software Foundation, Inc., 
// 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// 
package magellan.client.preferences;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import magellan.client.Client;
import magellan.client.swing.CenterLayout;
import magellan.client.swing.layout.GridBagHelper;
import magellan.client.swing.preferences.PreferencesAdapter;
import magellan.library.utils.Resources;
import magellan.library.utils.logging.Logger;

public class ClientFileHistoryPreferences extends JPanel implements PreferencesAdapter {
  private final Logger log = Logger.getInstance(ClientFileHistoryPreferences.class);
  protected JTextField txtFileHistorySize;
  protected Client source;
  protected Properties settings;

  /**
   * Creates a new HistoryPanel object.
   */
  public ClientFileHistoryPreferences(Client client, Properties settings) {
    source = client;
    this.settings = settings;
    initGUI();
  }

  private void initGUI() {
    // set up the panel for the maximum file history size
    this.setLayout(CenterLayout.SPAN_X_LAYOUT);

    JPanel help = new JPanel();
    help.setBorder(new TitledBorder(new CompoundBorder(BorderFactory.createEtchedBorder(),
                               new EmptyBorder(0, 3, 3, 3)),
                     Resources.get("clientpreferences.border.filehistory")));
    help.setLayout(new GridBagLayout());

    GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0);
    JLabel l = new JLabel( Resources.get("clientpreferences.lbl.filehistoryentries.caption"));
    help.add(l, con);
    con.gridx = 1;
    con.weightx = 1;
    txtFileHistorySize = new JTextField(Integer.toString(source.getMaxFileHistorySize()));
    txtFileHistorySize.setPreferredSize(new java.awt.Dimension(50, 20));

    //editFontSize.setMinimumSize(new java.awt.Dimension(50, 20));
    txtFileHistorySize.setMaximumSize(new java.awt.Dimension(50, 20));

    help.add(txtFileHistorySize, con);

    con.gridx = 0;
    con.gridy = 1;
    con.gridwidth = 2;

    JTextArea txtFoo = new JTextArea( Resources.get("clientpreferences.txt.filehistorydescription.text"));
    txtFoo.setLineWrap(true);
    txtFoo.setWrapStyleWord(true);
    txtFoo.setEditable(false);
    txtFoo.setOpaque(false);
    txtFoo.setSelectionColor(getBackground());
    txtFoo.setSelectedTextColor(getForeground());
    txtFoo.setFont(l.getFont());
    txtFoo.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Label.foreground"));
    help.add(txtFoo, con);

    // layout this container
    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    c.insets.top = 10;
    c.insets.bottom = 10;
    GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
                   GridBagConstraints.NORTHWEST,
                   GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

    // help panel
    this.add(help, c);
    c.insets.top = 0;
  }

  /**
   * DOCUMENT-ME
   *
   * 
   */
  public Component getComponent() {
    return this;
  }

  /**
   * DOCUMENT-ME
   *
   * 
   */
  public String getTitle() {
    return  Resources.get("clientpreferences.border.filehistory");
  }

      /**
       * TODO: implement it
       * @deprecated not implemented!
       * 
       * @see magellan.client.swing.preferences.PreferencesAdapter#initPreferences()
       */
      public void initPreferences() {
          // TODO: implement it
      }

  /**
   * DOCUMENT-ME
   */
  public void applyPreferences() {
    try {
      int i = Integer.parseInt(txtFileHistorySize.getText());
      source.setMaxFileHistorySize(i);
    } catch(NumberFormatException e) {
      log.error("ClientPreferences(): Unable to set maximum file history size", e);
    }
  }
}