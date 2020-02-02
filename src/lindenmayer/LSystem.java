package lindenmayer;

import org.json.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Class implementing AbstractLSystem. Refer to {@link AbstractLSystem} for methods' description.
 *
 * @author Sandrine Bédard et Robin Legault
 */
public class LSystem extends AbstractLSystem {

    private Map<Character, Symbol> alphabet;
    private Map<Symbol, ArrayList<ArrayList<Symbol>>> rules;
    private ArrayList<Symbol> axiom;
    private Map<Symbol, String> actions;

    /**
     * Constructor
     */
    public LSystem() {
        this.alphabet = new HashMap<>();
        this.rules = new HashMap<>();
        this.actions = new HashMap<>();
    }

    /**
     * Getters
     */
    public Map<Character, Symbol> getAlphabet() {
        return this.alphabet;
    }

    public Map<Symbol, ArrayList<ArrayList<Symbol>>> getRules() {
        return this.rules;
    }

    public Iterator<Symbol> getAxiom() {
        return this.axiom.iterator();
    }

    /**
     * Setters
     */
    public void setAxiom(String str) {
        ArrayList<Symbol> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            list.add(this.alphabet.get(str.charAt(i)));
        }
        this.axiom = list;
    }

    public void setAction(Symbol sym, String action) {
        this.actions.put(sym, action);
    }

    /**
     * Methods to initialize the LSystem
     */
    public Symbol addSymbol(char sym) {
        Symbol symbol = new Symbol(sym);
        this.getAlphabet().put(sym, symbol);

        return symbol;
    }

    public void addRule(Symbol sym, String expansion) {

        ArrayList<Symbol> list = new ArrayList<>();
        for (int i = 0; i < expansion.length(); i++) {
            list.add(this.alphabet.get(expansion.charAt(i)));
        }

        // No rule associated with symbol
        if (this.getRules().get(sym) == null) {
            ArrayList<ArrayList<Symbol>> symRules = new ArrayList<>();
            symRules.add(list);
            this.rules.put(sym, symRules);
        } else {
            // Symbol already has at least 1 rule associated with it
            // Add new rule in existing rulesList
            this.rules.get(sym).add(list);
        }
    }

    protected void readJSONFile(String filename, Turtle turtle) throws java.io.IOException, JSONException {

        // Read JSON file with JSONTokener
        JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename)));

        /* 1) Initialize parameters */
        JSONObject system_params = input.getJSONObject("parameters");
        JSONArray init_turtle = system_params.getJSONArray("start");

        turtle.init(new Point2D.Double(init_turtle.getDouble(0), init_turtle.getDouble(1)), init_turtle.getDouble(2));
        double unit_step = system_params.getDouble("step");
        double unit_angle = system_params.getDouble("angle");
        turtle.setUnits(unit_step, unit_angle);

        /* 2) Initialize alphabet */
        JSONArray system_alphabet = input.getJSONArray("alphabet");

        for (int i = 0; i < system_alphabet.length(); i++) {
            // Convert the object in character type and add in alphabet
            this.addSymbol(system_alphabet.get(i).toString().charAt(0));
        }

        /* 3) Initialize rules */
        JSONObject system_rules = input.getJSONObject("rules"); // Returns symbol and its rules
        JSONArray symbols = system_rules.names(); // Returns a table of all symbols

        // For all symbols
        for (int i = 0; i < symbols.length(); ++i) {

            String key = symbols.getString(i); // Return the symbol in string format (i.e. "F")
            Symbol symbol = this.getAlphabet().get(key.charAt(0)); // Convert key in symbol format
            JSONArray values = system_rules.getJSONArray(key); // Returns all rules associated with key

            // For all rules associated with 1 symbol
            for (int j = 0; j < values.length(); j++) {
                Object object = values.get(j);
                this.addRule(symbol, object.toString()); // Add the symbol and associated rule in rules
            }
        }

        /* 4) Initialize axiom */
        String system_axiom = input.getString("axiom");
        this.setAxiom(system_axiom);

        /* 5) Initialize actions */
        JSONObject system_actions = input.getJSONObject("actions"); // Returns symbol and associated action
        JSONArray symbols_actions = system_actions.names(); // Returns table of all symbols

        for (int i = 0; i < symbols_actions.length(); i++) {
            Object object = symbols_actions.get(i);
            String key = object.toString(); // Returns symbol in string format
            String value = system_actions.getString(key); // Returns action
            Symbol symbol = this.alphabet.get(key.charAt(0));  // Returns symbol in symbol format

            this.setAction(symbol, value); // Adds symbol : action in actions
        }
    }

    /**
     * Methods to execute
     */
    public Iterator<Symbol> rewrite(Symbol sym) {

        ArrayList<ArrayList<Symbol>> symRules = this.rules.get(sym);

        // If no rule was previously stored with addRule
        if (symRules == null) {
            return null;
        }
        // If a single rule was given, return the rule
        if (symRules.size() == 1) {
            Iterator<Symbol> symRule = symRules.get(0).iterator();
            return symRule;
        } // If multiples rules were given, return a rule randomly
        else {
            // Find a random index
            int index = this.RND.nextInt(symRules.size());
            Iterator<Symbol> randomRule = symRules.get(index).iterator();
            return randomRule;
        }
    }

    public void tell(Turtle turtle, Symbol sym) {

        String action = this.actions.get(sym);

        switch (action) {
            case "draw":
                turtle.draw();
                break;
            case "move":
                turtle.move();
                break;
            case "push":
                turtle.push();
                break;
            case "pop":
                turtle.pop();
                break;
            case "turnR":
                turtle.turnR();
                break;
            case "turnL":
                turtle.turnL();
                break;
            default:
                turtle.stay();
                break;
        }
    }

    /**
     * Advanced methods
     */
    public Iterator<Symbol> applyRules(Iterator<Symbol> sequence, int n) {

        // For each round of rewriting
        for (int i = 0; i < n; i++) {
            // Create new list to store the rewritten sequence
            ArrayList<Symbol> rewritten = new ArrayList<>();

            // Rewrite each symbol of sequence
            while (sequence.hasNext()) {
                Symbol sym = sequence.next();

                // Resulting sequence after rewrite(sym)
                Iterator<Symbol> rule = rewrite(sym);

                if (rule == null) { // If symbol has no rewriting rule
                    rewritten.add(sym); // Directly add sym in rewritten sequence
                } else {
                    // For each symbol, add resulting sequence in rewritten
                    while (rule.hasNext()) {
                        rewritten.add(rule.next());
                    }
                }
            }
            // After each round, rewritten sequence becomes the starting sequence
            sequence = rewritten.iterator();
        }
        return sequence;
    }

    public void tell(Turtle turtle, Symbol sym, int n) {

        // Base case
        // No rewriting needs to be done, directly call method on turtle
        if (n == 0) {
            tell(turtle, sym);
        } else {
            // Rewriting has to be done since n > 0
            // Perform 1 round of rewriting
            Iterator<Symbol> sequence = rewrite(sym);

            if (sequence == null) { // If symbol has no rewriting rule
                tell(turtle, sym);
            } else {
                // For each symbol of the rewritten sequence
                while (sequence.hasNext()) {
                    // Recursive call
                    // Perform rewriting with n = n - 1
                    tell(turtle, sequence.next(), n - 1);
                }
            }
        }
    }

    /**
     * Method used when calling getBoundingBox.
     * Used to calculate the rectangle that would bound the drawing after multiple rounds of rewriting.
     *
     * @param turtle turtle used for drawing
     * @param sym the starting sequence in round 0: a single symbol
     * @param n number of rounds
     * @param rec rectangle that was bounding the drawing before action
     * @return new rectangle that is bounding the drawing after action
     */
    public Rectangle2D tell(Turtle turtle, Symbol sym, int n, Rectangle2D rec) {

        // Base case
        // No rewriting needs to be done, directly call method on turtle
        if (n == 0) {
            tell(turtle, sym);
            Point2D newPosition = turtle.getPosition();
            // Once turtle performed action, check if newPosition is contained in previous rec
            // If not, add newPosition in rec to get covering rec
            if (!rec.contains(newPosition)) {
                rec.add(newPosition);
            }
            return rec;

        } else {
            // Rewriting has to be done
            // Perform 1 round of rewriting
            Iterator<Symbol> sequence = rewrite(sym);

            if (sequence == null) { // If symbol has no rewriting rule
                return (tell(turtle, sym, 0, rec));
            }
            // For each symbol of the rewritten sequence
            // Perform recursive call
            do {
                rec.add(tell(turtle, sequence.next(), n - 1, rec));
            } while (sequence.hasNext());
        }
        return rec;
    }

    public Rectangle2D getBoundingBox(Turtle turtle, Iterator<Symbol> seq, int n) {

        Point2D position = turtle.getPosition();
        Rectangle2D rec = new Rectangle2D.Double(position.getX(), position.getY(), 0, 0);

        // For each symbol of sequence
        // Add resulting rec after action
        while (seq.hasNext()) {
            rec.add(tell(turtle, seq.next(), n, rec));
        }
        return rec;
    }
}