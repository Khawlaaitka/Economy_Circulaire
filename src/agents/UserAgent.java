package agents;

import data.Product;
import data.ProductType;
import gui.UserAgentWindow;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**class related to the user, owner of products to repair
 * @author emmanueladam
 * */
public class UserAgent extends GuiAgent {
    /*list of products to repair/
    List<Product> products;
    /*general skill of repairing/
    int skill;
    /*gui window/
    UserAgentWindow window;

    private LocalDate extract(String content) {
        // Cette fonction permet d'extraire la date proposée à partie du message reçu de la part du Repaire Coffee
        //List<LocalDate> dates = new ArrayList<>();
        String[] tokens = content.split(": ");
        if (tokens.length > 1) {
            String dateString = tokens[1].trim().split(" ")[0];
            return LocalDate.parse(dateString);
        }

        return null;
    }
    class Proposition extends CyclicBehaviour {
        public void action() {
            ACLMessage msgProp = receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
            if (msgProp != null) {
                String msgcontent = msgProp.getContent();
                String sender = msgProp.getSender().getLocalName();
                LocalDate DateProp = extract(msgcontent);
                println("Tu viens de recevoir une proposiition de la part de " + sender + ": " + msgcontent);

                propositions.put(msgProp.getSender(), DateProp);
                System.out.println("Vous avez reçu ces propositions: " + DateProp);
            } else {
                block();
            }
            // On fait appelle à la fonction TrouverPPDate() pour trouver la dates la plus proche
            List<LocalDate> DatePP = TrouverPPDate();


            List<AID> RCPlusProche = new ArrayList<>();
            for (Map.Entry<AID, LocalDate> entry : propositions.entrySet()) {
                if (DatePP.contains(entry.getValue())) {
                    RCPlusProche.add(entry.getKey());
                }
            }

            if (!RCPlusProche.isEmpty()) {
                println("Les repaire coffee avec les dates les plus proches sont : ");
                for (AID agent : RCPlusProche) {
                    println(agent.getLocalName());
                }
            } else {
                println("Aucun Repaire Coffee trouvé");
            }
        }

        private List<LocalDate> TrouverPPDate() {
            if (propositions.isEmpty()) {
                return Collections.emptyList();
            }

            LocalDate now = LocalDate.now(); //Date et Heure du moment
            List<LocalDate> dates = new ArrayList<>(propositions.values());
            long nearest = dates.stream()
                    .mapToLong(date -> Math.abs(ChronoUnit.DAYS.between(now, date)))
                    .min().orElse(0);

            return dates.stream()
                    .filter(date -> Math.abs(ChronoUnit.DAYS.between(now, date)) == nearest)
                    .collect(Collectors.toList());
        }

    }

    Map<AID, LocalDate> propositions = new HashMap<>();


    @Override
    public void setup()
    {
        this.window = new UserAgentWindow(getLocalName(),this);
        window.setButtonActivated(true);
        //add a random skill
        Random hasard = new Random();
        skill = hasard.nextInt(5);
        println("hello, I have a skill = "+ skill);
        //add some products choosen randomly in the list Product.getListProducts()
        products = new ArrayList<>();
        int nbTypeOfProducts = ProductType.values().length;
        int nbPoductsByType = Product.NB_PRODS / nbTypeOfProducts;
        var existingProducts = Product.getListProducts();
        //add products
        for(int i=0; i<nbTypeOfProducts; i++)
            if(hasard.nextBoolean())
                products.add(existingProducts.get(hasard.nextInt(nbPoductsByType) + (i*nbPoductsByType)));
        //we need at least one product
        if(products.isEmpty())  products.add(existingProducts.get(hasard.nextInt(nbPoductsByType*nbTypeOfProducts)));
        window.addProductsToCombo(products);
        println("Here are my objects : ");
        products.forEach(p->println("\t"+p));

    }

    /*the window sends an evt to the agent/
    @Override
    public void onGuiEvent(GuiEvent evt) {

        // Check if it is the OK button event
        if (evt.getType() == UserAgentWindow.OK_EVENT) {
            List<ProductType> ProductTypes = new ArrayList<>();
            for (Product product : products) {
                ProductTypes.add(product.getType());
            }
            String productsContent = ProductTypes.stream()
                    .map(Enum::name) // Convertir les types de produits en chaîne de caractères
                    .collect(Collectors.joining(","));
            var RepaireCofee = AgentServicesTools.searchAgents(this, "repair", "coffee");
            println("-".repeat(30));


            for (AID repairCoffeeAgent : RepaireCofee) {

                ACLMessage message = new ACLMessage(ACLMessage.CFP);

                // Crèation et envoi d'un message à chaque Repaire Coffee de façon individuel
                message.addReceiver(repairCoffeeAgent.getLocalName());
                message.setContent("J'ai un problème avec ces produits: " +productsContent);
                send(message);
                println("found this repair coffee: " + repairCoffeeAgent.getLocalName());

                println("-".repeat(30));

        }
            addBehaviour(new Proposition());


        }


    }



    public void println(String s){window.println(s);}

    @Override
    public void takeDown(){println("bye !!!");}
}
