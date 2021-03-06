package iul.iscte.tsio.view.panels;

import iul.iscte.tsio.controller.UsersController;
import iul.iscte.tsio.interfaces.Refreshable;
import iul.iscte.tsio.model.UserEntity;
import iul.iscte.tsio.utils.Labels;
import iul.iscte.tsio.view.optionPanes.AddFriendOptionPane;
import iul.iscte.tsio.view.optionPanes.DeleteFriendOptionPane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class UserFriendsListPanel extends JPanel implements Refreshable {

	private static final long serialVersionUID = 1L;
	private UserEntity loggedUser;
	private JButton addFriend;
	private JButton deleteFriend;
	private JList<UserEntity> friendsList;
	private DefaultListModel<UserEntity> listModel;

	public UserFriendsListPanel(UserEntity loggedUser) {
		this.loggedUser = loggedUser;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 5, 5, 5));
		
		JPanel aux = new JPanel();
		aux.setLayout(new FlowLayout());
		aux.add(addFriend = new JButton(Labels.ADDFRIENDBUTTON.getValue()));
		addFriend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AddFriendOptionPane();
				refresh();
			}
		});
		aux.add(deleteFriend = new JButton(Labels.DELETEFRIENDBUTTON.getValue()));
		deleteFriend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new DeleteFriendOptionPane();
				refresh();
			}
		});
		add(aux, BorderLayout.SOUTH);
		add(new JLabel(Labels.MYFRIENDS.getValue()), BorderLayout.NORTH);

		listModel = new DefaultListModel<UserEntity>();
		friendsList = new JList<UserEntity>(listModel);
		friendsList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		add(new JScrollPane(friendsList), BorderLayout.CENTER);
		refresh();
	}

	@Override
	public void refresh() {
		listModel.clear();
		List<UserEntity> data = UsersController.getInstance().getAllFriends(
				loggedUser);
		for (UserEntity userEntity : data) {
			listModel.addElement(userEntity);
		}

	}

}
