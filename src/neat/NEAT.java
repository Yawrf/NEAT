
package neat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import visualizer.GenomeVisualizer;

/**
 *
 * @author rewil
 */
public class NEAT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        int countInputs = 10;
        int countOutputs = 10;
        int countMutations = 500;
        int countGenerations = 10;
        
        SpeciesManipulator sm = new SpeciesManipulator(countInputs, countOutputs);
        
        for(int gen = 0; gen < countGenerations; ++gen) {
            System.out.println("\nGeneration #" + (gen+1));
            sm.process(countMutations);
            System.out.println(sm);
        }
        
        Genome.storeGenome(sm.getSpecies()[0].getMascot());
        GenomeVisualizer gv = new GenomeVisualizer();
        sm.setInputs(1,2,3,4,5,6,7,8,9,10);
        gv.main(args);
        
    }
    
    private static Map<Integer, Map<Integer, Integer>> connectInnos = new HashMap<>();
    private static int connectCount = 0;
    
    public static Integer getConnectInno(Integer in, Integer out) {
        if(connectInnos.containsKey(in)) {
            Map<Integer, Integer> temp = connectInnos.get(in);
            if(temp.containsKey(out)) {
                return temp.get(out);
            } else {
                temp.put(out, connectCount);
                return connectCount++;
            }
        } else {
            Map<Integer, Integer> temp = new HashMap<>();
            connectInnos.put(in, temp);
            temp.put(out, connectCount);
            return connectCount++;
        }
    }
    
}
