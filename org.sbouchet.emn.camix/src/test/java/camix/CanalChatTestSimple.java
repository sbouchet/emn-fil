package camix;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de tests unitaires de la classe CanalChat (verion simple).
 *
 * @version 3.0
 * @author Matthias Brun
 *
 */
public class CanalChatTestSimple extends TestCase
{
	/**
	 * Client util pour les tests.
	 */
	private ClientChat clientMock;

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
		/* rien à faire */
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
	 * Teste l'ajout d'un client non présent dans le canal.
	 * 
	 * <p>Méthode concernée : public void ajouteClient(IClientChat client)</p>
	 *
	 */
	@Test
	public void testAjouteClient_nonpresent()
	{
		/* Création d'un canal. */
		final CanalChat canal = new CanalChat("canal test ajoute client non présent");

		/* Création d'un identifiant pour le client. */
		final String id = "clientIdAjouteClientNonPresent";
		
		/* Description du comportement du mock du client                            */
		/* Le mock du client est sollicité trois fois pour en connaître la socket : */
		/* - deux fois pour l'insertion ;                                           */ 
		/* - une fois pour l'assert de présence dans le canal.                      */
		final int repetitions = 3;
		
		EasyMock.expect(
				this.clientMock.donneId()
				).andReturn(
						id
			);
		
		EasyMock.expectLastCall().times(repetitions);

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		/* Exécution de la méthode à tester. */
		canal.ajouteClient(this.clientMock);

		/* Assertions. */
		assertEquals("Nombre de clients incompatible", 1, (int) canal.donneNombreClients());
		
		assertTrue("Le client n'est pas dans le canal", canal.estPresent(this.clientMock));

		/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}

	/**
	 * Teste l'ajout d'un client déjà présent dans le canal.
	 * 
	 * <p>Méthode concernée : public void ajouteClient(IClientChat client)</p>
	 *
	 */
	@Test
	public void testAjouteClient_present()
	{
		/* Création d'un canal. */
		final CanalChat canal = new CanalChat("canal test ajoute client présent");

		/* Création d'un identifiant pour le client. */
		final String id = "clientIdAjouteClientPresent";

		/* Description du comportement du mock du client                            */
		/* (le mock du client est sollicité cinq fois pour en connaître la socket). */
		final int repetitions = 5;
		
		EasyMock.expect(
				this.clientMock.donneId()
				).andReturn(
						id
			);
		
		EasyMock.expectLastCall().times(repetitions);

		/* Chargement du mock du client. */
		EasyMock.replay(this.clientMock);

		/* Premier ajout du client dans le canal. */
		canal.ajouteClient(this.clientMock);

		/* Assertions */
		assertEquals("Nombre de clients incompatible après première insertion", 1, (int) canal.donneNombreClients());
		
		assertTrue("Le client n'est pas dans le canal après première insertion", canal.estPresent(this.clientMock));

		/* Seconde insertion du client dans le canal. */
		canal.ajouteClient(this.clientMock);

		/* Assertions. */
		assertEquals("Nombre de client incompatible après seconde insertion", 1, (int) canal.donneNombreClients());
	
		assertTrue("Le client n'est pas dans le canal après seconde insertion", canal.estPresent(this.clientMock));

		/* Vérification du comportement du mock. */
		EasyMock.verify(this.clientMock);
	}
	
	/**
	 * Test la méthode d'envoie de messages aux clients du canal.
	 * 
	 * <p>Méthode concernée : public void envoieClients(String message) </p>
	 */
	@Test
	public void testEnvoieClients()
	{
		/* Le message de test. */
		final String message = "Message de test.";
		
		/* Création d'un canal. */
		final CanalChat canal = new CanalChat("canal test envoie clients");
		
		/* Création des mocks des clients 1 et 2. */
		final int nombreDeClients = 2;
		final ClientChat clientMock1 = EasyMock.createMock(ClientChat.class);
		final ClientChat clientMock2 = EasyMock.createMock(ClientChat.class);
		
		/* Création d'un identifiant pour les clients 1 et 2. */
		final String id1 = "clientIdEnvoieClient1";
		final String id2 = "clientIdEnvoieClient2";
		
		/* Description du comportement des mocks des clients 1 zt 2             */
		/* (les mocks sont sollicités trois fois pour en connaître les sockets) */
		final int repetitions = 3;
		
		EasyMock.expect(
				clientMock1.donneId()
				).andReturn(
						id1
			);
		
		EasyMock.expectLastCall().times(repetitions);

		EasyMock.expect(
				clientMock2.donneId()
				).andReturn(
						id2
			);
		
		EasyMock.expectLastCall().times(repetitions);
		
		/* Chargement des mocks des clients 1 et 2. */
		EasyMock.replay(clientMock1);
		EasyMock.replay(clientMock2);
		
		/* Ajout des clients 1 et 2 dans le canal. */
		canal.ajouteClient(clientMock1);
		canal.ajouteClient(clientMock2);

		/* Assertions. */
		assertEquals("Nombre de clients incompatible après insertions des clients", 
				nombreDeClients, (int) canal.donneNombreClients());
		
		assertTrue("Le client 1 n'est pas dans le canal", canal.estPresent(clientMock1));
		assertTrue("Le client 2 n'est pas dans le canal", canal.estPresent(clientMock2));
	
		/* Vérification du comportement des mocks. */
		EasyMock.verify(clientMock1);
		EasyMock.verify(clientMock2);

		/* Réinitialisation des mocks des clients 1 et 2. */
		EasyMock.reset(clientMock1);
		EasyMock.reset(clientMock2);
		
		/* Description des comportements des mocks des clients 1 et 2 pour l'envoie d'un message. */
		clientMock1.envoieMessage(message);
		clientMock2.envoieMessage(message);

		/* Chargement des mocks des clients 1 et 2. */
		EasyMock.replay(clientMock1);
		EasyMock.replay(clientMock2);
		
		/* Méthode à tester. */
		canal.envoieClients(message);
		
		/* Vérification du comportement des mocks. */
		EasyMock.verify(clientMock1);
		EasyMock.verify(clientMock2);
	}
}


