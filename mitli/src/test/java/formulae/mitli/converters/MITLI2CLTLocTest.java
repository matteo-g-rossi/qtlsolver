/**
 * 
 */
package formulae.mitli.converters;

import static org.junit.Assert.*;

import org.junit.Test;

import formulae.mitli.atoms.MITLIAtom;
import formulae.mitli.atoms.MITLIPropositionalAtom;

/**
 * @author Claudio1
 *
 */
public class MITLI2CLTLocTest {

	@Test
	public void test() {
		
		MITLIAtom a=new MITLIPropositionalAtom("a");
		
		
		MITLI2CLTLoc converter=new MITLI2CLTLoc(a, 5);
		
		converter.apply();
		converter.getTheta(a);
	}

}
