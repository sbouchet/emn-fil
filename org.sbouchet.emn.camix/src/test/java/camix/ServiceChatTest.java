package camix;

import java.lang.reflect.Field;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de tests unitaires de la classe ServiceChat.
 *
 * @version 3.0
 * @author Matthias Brun
 *
 */
public class ServiceChatTest extends TestCase 
{
	/**
	 * Nom de l'attribut de l'ensemble des canaux dans la classe ServiceChat.
	 * Nécessaire pour la réflexion permettant d'accéder à cet attribut privé.
	 */
	private static final String NOM_ATTRIBUT_CANAUX = "canaux";
	
	/**
	 * Client utile pour les tests.
	 */
	private ClientChat clientMock;
	
	/**
	 * Nom du canal par défaut.
	 */
	private String nomCanalDefaut;
	
	/**
	 * Crée le client nécessaire aux tests.
	 *
	 * <p>Code exécuté avant les tests.</p>
	 *
	 * @throws Exception toute exception.
	 *
	 */
	@Before
	public void setUp() throws Exception
	{
		/* Création du mock client */
		this.clientMock = EasyMock.createMock(ClientChat.class);
		
		/* Récupération du nom du canal par défaut */
		this.nomCanalDefaut = Camix.CONFIGURATION.getString("CANAL_PAR_DEFAUT");
	}

	/**
	 * Non implanté.
	 *
	 * <p>Code exécuté après les tests.</p>
	 *
	 * @throws Exception toute exception.
	 *
	 */
	@After
	public void tearDown() throws Exception 
	{
		// rien à faire
	}

	/**
	 * Pré-condition d'existence du mock de client.
	 */
	@Test
	public void testPreconditions() 
	{
		/* Assertions. */
		assertNotNull(this.clientMock);
	}

	/**
	 * Teste l'information de départ d'un client.
	 * 
	 * <p>Méthode concernée : public void informeDepartClient(IClientChat client)</p>
	 */
	@Test
	public void testInformeDepartClient()
	{
		// Création d'un service avec le canal par défaut de Camix.
		final ServiceChat service = new ServiceChat(this.nomCanalDefaut);
		
		final String clientSurnom = "toto";

		/* Description du comportement du mock du client                                  */
		/* (le mock du client est sollicité pour avoir le nom du client)                  */
		EasyMock.expect(
				this.clientMock.donneSurnom()
				).andReturn(
						clientSurnom
			);
		
		/* (le mock du client est sollicité pour afficher le message de départ aux contacts) */
		this.clientMock.envoieContacts(
			String.format(ProtocoleChat.MESSAGE_DEPART_CHAT, clientSurnom)
		);

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		/* Exécution de la méthode à tester. */
		service.informeDepartClient(this.clientMock);
    	
		/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}
	
	/**
	 * Teste l'ajout d'un canal non présent dans le chat.
	 * 
	 * <p>Méthode concernée : public void ajouteCanal(IClientChat client, String nom)</p>
	 */
	@Test
	public void testAjouteCanal_nonpresent()
	{
		// Création d'un service avec le canal par défaut de Camix.
		final ServiceChat service = new ServiceChat(this.nomCanalDefaut);
		
		// Nom du canal à ajouter.
		final String nomNouveauCanal = "nouveau canal nom présent";
		
		/* Description du comportement du mock du client                                  */
		/* (le mock du client est sollicité pour afficher le message de céation du canal) */
		this.clientMock.envoieMessage(String.format(ProtocoleChat.MESSAGE_CREATION_CANAL, nomNouveauCanal));

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		/* Exécution de la méthode à tester. */
		service.ajouteCanal(this.clientMock, nomNouveauCanal);
    	
		try {
			/* Pour l'accès aux canaux (private) du service. */
			final Field attributCanaux = ServiceChat.class.getDeclaredField(NOM_ATTRIBUT_CANAUX);
			attributCanaux.setAccessible(true);
				
			@SuppressWarnings("unchecked")
			final Hashtable<String, CanalChat> canaux = (Hashtable<String, CanalChat>) attributCanaux.get(service);

			/* Assertions. */
			assertNotNull("Le canal n'est pas dans le chat.", canaux.get(nomNouveauCanal));
		} 
		catch (SecurityException e) {
			fail("Problème de sécurité sur la réflexion (ajoute canal nom présent).");
		} 
		catch (NoSuchFieldException e) {
			fail("Attribut concerné non existant (ajoute canal nom présent).");
		} 
		catch (IllegalArgumentException e) {
			fail("Arguments de la méthode de réflexion invalides (ajoute canal nom présent).");
		} 
		catch (IllegalAccessException e) {
			fail("Accès illégal à l'attribut concerné (ajoute canal nom présent).");
		}
		
    	/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}

	/**
	 * Teste l'ajout d'un canal déjà présent dans le chat.
	 * 
	 * <p>Méthode concernée : public void ajouteCanal(IClientChat client, String nom)</p>
	 */
	@Test
	public void testAjouteCanal_present()
	{
		// Création d'un service avec le canal par défaut de Camix.
		final ServiceChat service = new ServiceChat(this.nomCanalDefaut);
		
		// Nom du canal à ajouter.
		final String nomNouveauCanal = "nouveau canal présent";
		
		/* Description du comportement du mock du client                                                    */
		/* (le mock du client est sollicité une première fois pour afficher le message de céation du canal) */
		this.clientMock.envoieMessage(String.format(ProtocoleChat.MESSAGE_CREATION_CANAL, nomNouveauCanal));
		/* (le mock du client est sollicité une deuxièmre fois pour afficher le message de présence du canal) */
		this.clientMock.envoieMessage(String.format(ProtocoleChat.MESSAGE_CREATION_IMPOSSIBLE_CANAL, nomNouveauCanal));

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		/* Premier ajout du canal dans le chat. */
		service.ajouteCanal(this.clientMock, nomNouveauCanal);
    	
		try {
			/* Pour l'accès aux canaux (private) du service. */
			final Field attributCanaux = ServiceChat.class.getDeclaredField(NOM_ATTRIBUT_CANAUX);
			attributCanaux.setAccessible(true);
				
			@SuppressWarnings("unchecked")
			final Hashtable<String, CanalChat> canaux = (Hashtable<String, CanalChat>) attributCanaux.get(service);
				
			/* Assertions. */
			assertNotNull("Le premier canal canal n'est pas dans le chat.", canaux.get(nomNouveauCanal));
			
			/* Récupération du canal ajouté. */
			final CanalChat premierNouveauCanal = canaux.get(nomNouveauCanal);
	    	
			/* Second ajout du canal dans le chat. */
			service.ajouteCanal(this.clientMock, nomNouveauCanal);
	    	
	    	/* Récupération du canal homonyme. */
			final CanalChat secondNouveauCanal = canaux.get(nomNouveauCanal);
	    	
	    	/* Assertions. */
			assertNotNull("Le canal n'est plus dans le chat.", canaux.get(nomNouveauCanal));
			assertSame("Le second canal a substitué le premier.", premierNouveauCanal, secondNouveauCanal);
				
		} 
		catch (SecurityException e) {
			fail("Problème de sécurité sur la réflexion (ajoute cana présent).");
		} 
		catch (NoSuchFieldException e) {
			fail("Attribut concerné non existant (ajoute cana présent).");
		} 
		catch (IllegalArgumentException e) {
			fail("Arguments de la méthode de réflexion invalides (ajoute cana présent).");
		} 
		catch (IllegalAccessException e) {
			fail("Accès illégal à l'attribut concerné (ajoute cana présent).");
		}
    	
    	/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}
	
	/**
	 * Teste l'ajout du canal par défaut dans le chat.
	 * 
	 * <p>Méthode concernée : public void ajouteCanal(IClientChat client, String nom)</p>
	 */
	@Test
	public void testAjouteCanal_canaldefaut()
	{
		// Création d'un service avec le canal par défaut de Camix.
		final ServiceChat service = new ServiceChat(this.nomCanalDefaut);
		
		// Nom du canal à ajouter.
		final String nomNouveauCanal = this.nomCanalDefaut;
		
		/* Description du comportement du mock du client    
		/* (le mock du client est sollicité une fois pour afficher le message de présence du canal) */
		this.clientMock.envoieMessage(String.format(ProtocoleChat.MESSAGE_CREATION_IMPOSSIBLE_CANAL, nomNouveauCanal));

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		try {
			/* Pour l'accès aux canaux (private) du service. */
			final Field attributCanaux = ServiceChat.class.getDeclaredField(NOM_ATTRIBUT_CANAUX);
			attributCanaux.setAccessible(true);
				
			@SuppressWarnings("unchecked")
			final Hashtable<String, CanalChat> canaux = (Hashtable<String, CanalChat>) attributCanaux.get(service);
			
	    	/* Récupération du canal par défaut. */
			final CanalChat canalParDefaut = canaux.get(nomNouveauCanal);
	    	
			/* Exécution de la méthode à tester. */
			service.ajouteCanal(this.clientMock, nomNouveauCanal);

	    	/* Récupération du canal homonyme. */
			final CanalChat secondNouveauCanal = canaux.get(nomNouveauCanal);
	    	
	    	/* Assertions. */
			assertNotNull("Le canal par défaut n'est plus dans le chat.", canaux.get(nomNouveauCanal));
			assertSame("Le second canal a substitué le canal par défaut.", canalParDefaut, secondNouveauCanal);
			
		} 
		catch (SecurityException e) {
			fail("Problème de sécurité sur la réflexion (ajout canal défaut).");
		} 
		catch (NoSuchFieldException e) {
			fail("Attribut concerné non existant (ajout canal défaut).");
		} 
		catch (IllegalArgumentException e) {
			fail("Arguments de la méthode de réflexion invalides (ajout canal défaut).");
		} 
		catch (IllegalAccessException e) {
			fail("Accès illégal à l'attribut concerné (ajout canal défaut).");
		}
    	
    	/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}

}
