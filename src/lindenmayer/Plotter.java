/*
 * Copyright 2020 Mikl&oacute;s Cs&#369;r&ouml;s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lindenmayer;

import java.awt.geom.Point2D;
import java.io.PrintStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * Entry point for drawing L-System in PostScript. Call with command-line
 * arguments: json file specifying and number of iterations:
 * </code>java ... Plotter [-o output.ps] lsystem.json n</code>
 *
 * @author Miklos Csuros
 */
public class Plotter {

    private PrintStream out = System.out;
    private LSystem lsystem;
    private EPSTurtle turtle;

    // For tests
    private Plotter() {
        // this(new LSystem(), new EPSTurtle());
    }

    /**
     * Instance-linked main.
     *
     * @param args
     * @throws Exception
     */
    private void allezallez(String[] args) throws Exception {
        int arg_idx = 0;
        while (arg_idx < args.length && args[arg_idx].startsWith("-")) {
            String arg_key = args[arg_idx++];
            if (arg_idx == args.length) {
                throw new IllegalArgumentException("Missing value for option " + arg_key);
            }
            if ("-o".equals(arg_key)) {
                String output_file = args[arg_idx++];
                out = "=".equals(output_file) ? System.out : new java.io.PrintStream(output_file);
            } else {
                throw new IllegalArgumentException("Switch " + arg_key + " not recognized (-o output.ps)");
            }
        }

        if (arg_idx == args.length) {
            throw new IllegalArgumentException("Give json file name as command-line argument: java ... " + getClass().getName() + " lsystem.json niter");
        }
        String json_file = args[arg_idx++];
        if (arg_idx == args.length) {
            throw new IllegalArgumentException("Give number of rewriting iterations as the last command-line argument: java ... " + getClass().getName() + " lsystem.json niter");
        }
        int n_iter = Integer.parseInt(args[arg_idx++]);

        this.lsystem = new LSystem();
        this.turtle = new EPSTurtle(new GhostTurtle(), out);

        this.lsystem.readJSONFile(json_file, this.turtle);

        turtle.plot(lsystem, n_iter);
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(new Locale("en", "US")); // Pour forcer l'impression de la sortie avec le séparateur '.' plutôt que ','
        Plotter P = new Plotter();
        P.allezallez(args);
    }
}
