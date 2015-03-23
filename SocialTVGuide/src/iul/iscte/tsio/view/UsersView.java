package iul.iscte.tsio.view;

import iul.iscte.tsio.controller.UsersController;
import iul.iscte.tsio.server.Server;
import iul.iscte.tsio.utils.Labels;
import iul.iscte.tsio.view.panels.BasicActionsPanel;
import iul.iscte.tsio.view.panels.UserFriendsListPanel;
import iul.iscte.tsio.view.panels.UserInformationPanel;
import iul.iscte.tsio.view.program.UserRecommendationsListPanel;
import iul.iscte.tsio.view.search.SearchPanel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class UsersView extends JFrame {

	private static final long serialVersionUID = 1L;
	private SearchPanel searchPanel;
	private BasicActionsPanel basicActions;
	private UserFriendsListPanel frendsList;
	private UserInformationPanel userInformation;
	private UserRecommendationsListPanel userRecommendations;

	private static UsersView instance;

	private UsersView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setTitle(Labels.USERSVIEWTITLE.getValue());
		getContentPane().setLayout(new BorderLayout());
		buildView();
	}
	
	private void buildView() {
		getContentPane().add(searchPanel = new SearchPanel(),
				BorderLayout.NORTH);
		
		getContentPane().add(frendsList = new UserFriendsListPanel(Server.getInstance().getLoggedUser()),
				BorderLayout.EAST);
		
		JPanel aux = new JPanel();
		aux.setLayout(new BorderLayout());
		aux.setBorder(BorderFactory.createMatteBorder(
                1, 5, 1, 1, Color.gray));
		getContentPane().add(aux);
		
		aux.add(userInformation = new UserInformationPanel(Server.getInstance().getLoggedUser()), BorderLayout.CENTER);
		
		aux.add(basicActions = new BasicActionsPanel(), BorderLayout.SOUTH);
		//aux.add(userRecommendations = new UserRecommendationsList(UsersController.getInstance().getLoggedUser()));
		getContentPane().add(userRecommendations = new UserRecommendationsListPanel(Server.getInstance().getLoggedUser()),
				BorderLayout.SOUTH);
		
	}

	public static UsersView getInstance() {
		if (instance == null) {
			instance = new UsersView();
		}
		return instance;
	}

	public SearchPanel getSearchPanel() {
		return searchPanel;
	}

	public BasicActionsPanel getBasicActions() {
		return basicActions;
	}

	public UserFriendsListPanel getFrendsList() {
		return frendsList;
	}

	public UserInformationPanel getUserInformation() {
		return userInformation;
	}

	public UserRecommendationsListPanel getUserRecommendations() {
		return userRecommendations;
	}

}
