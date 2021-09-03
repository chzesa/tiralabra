package app.fortune;

import app.vector.*;
import app.parse.*;
import app.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class FortuneTest
{
	void test(Vector[] sites)
	{
		Fortune f = new Fortune(sites);
		Validator valid = new Validator(sites, f.processAll());
		assertTrue(valid.result());
	}


	void testv(List<Vector> sites)
	{
		Fortune f = new Fortune(sites);
		Validator valid = new Validator(sites, f.processAll());
		assertTrue(valid.result());
	}

	@Test
	public void testNone()
	{
		test(new Vector[] {});
	}

	@Test
	public void testOne()
	{
		test(new Vector[] {
			new Vector(1, 1)
		});
	}

	@Test
	public void testFew()
	{
		test(new Vector[] {
			new Vector(1, 1),
			new Vector(2, 3),
			new Vector(1, 2)
		});
	}

	@Test
	public void testVertical()
	{
		test(new Vector[] {
			new Vector(0, 5),
			new Vector(0, 4),
			new Vector(0, 3),
			new Vector(0, 2),
			new Vector(0, 1),
		});
	}
	/*
		Some tests are disabled since the validator uses the generated list of sites while
		the fortune algorithm may shift the first point upwards to avoid a degeneracy. This
		isn't accounted for by the validator and results in errors.
	*/

	/* Input is corrected */
	// @Test
	// public void testHorizontal()
	// {
	// 	test(new Vector[] {
	// 		new Vector(0, 0),
	// 		new Vector(1, 0),
	// 		new Vector(2, 0),
	// 		new Vector(3, 0),
	// 		new Vector(4, 0),
	// 	});
	// }

	@Test
	public void testHorizontalWithInitialSiteAbove()
	{
		test(new Vector[] {
			new Vector(1, 1),
			new Vector(0, 0),
			new Vector(1, 0),
			new Vector(2, 0),
			new Vector(3, 0),
			new Vector(4, 0),
		});
	}

	@Test
	public void testOverlappingCircleSiteEvents()
	{
		test(new Vector[] {
			new Vector(1.5, 3),
			new Vector(0, 0),
			new Vector(0, 1),
			new Vector(1, 0),
			new Vector(1, 2),
			new Vector(2, 0),
			new Vector(2, 2),
			new Vector(3, 0),
			new Vector(3, 1),
		});
	}

	/* Input is corrected */
	// @Test
	// public void testGrid()
	// {
	// 	test(new Vector[] {
	// 		new Vector(0, 0),
	// 		new Vector(0, 1),
	// 		new Vector(1, 0),
	// 		new Vector(1, 1),
	// 		new Vector(2, 0),
	// 		new Vector(2, 1),
	// 		new Vector(3, 0),
	// 		new Vector(3, 1),
	// 	});
	// }

	@Test
	public void testGridWithInitialSiteAbove()
	{
		test(new Vector[] {
			new Vector(1.5, 3),
			new Vector(0, 0),
			new Vector(0, 1),
			new Vector(1, 0),
			new Vector(1, 1),
			new Vector(2, 0),
			new Vector(2, 1),
			new Vector(3, 0),
			new Vector(3, 1),
		});
	}

	@Test
	public void testConvergingLeft()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1)
		});
	}

	@Test
	public void testConvergingRight()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0)
		});
	}

	@Test
	public void testConvergingWithParallels()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0)
		});
	}

	@Test
	public void testConvergingWithPointBelow()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, -2)
		});
	}

	@Test
	public void testConvergingWithPointBelowOnCircle()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, -1)
		});
	}

	@Test
	public void testConvergingWithParallelsAndPointBelow()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0),
			new Vector(0, -2)
		});
	}

	@Test
	public void testConvergingWithParallelsAndPointBelowOnCircle()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-1, 0),
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(1, 0),
			new Vector(0, -1)
		});
	}

	@Test
	public void testConvergingWithSiteOverlappingPoint()
	{
		double x = 1.0 / Math.sqrt(2);

		test(new Vector[] {
			new Vector(-x, x),
			new Vector(0, 1),
			new Vector(x, x),
			new Vector(0, 0)
		});
	}

	List<List<Vector>> loadDataset(String file)
	{
		String s = FileHandler.readToString(file);
		if (s.equals(""))
			throw new Error("Failed to locate test data.");

		return Parse.fromStringll(s);
	}

	void testDataset(String file)
	{
		List<List<Vector>> ll = loadDataset("dataset/" + file);
		for (List<Vector> sites : ll)
			assertTrue(new Validator(sites, new Fortune(sites).processAll()).result());
	}

	// Regression testing with known correct output

	@Test
	public void testDataset1()
	{
		testDataset("dataset1.txt");
	}

	@Test
	public void testDataset2()
	{
		testDataset("dataset2.txt");
	}

	@Test
	public void testDataset3()
	{
		testDataset("dataset3.txt");
	}

	@Test
	public void testDataset4()
	{
		testDataset("dataset4.txt");
	}

	@Test
	public void testDataset5()
	{
		testDataset("dataset5.txt");
	}

	@Test
	public void testDataset6()
	{
		testDataset("dataset6.txt");
	}

	/* False positive involving a nearly zero length edge */
	// @Test
	// public void test1()
	// {
	// 	String points = "0.3738787059895363 0.7343900047822423 0.6107491002798314 0.3055352702412732 0.8186640955523411 0.595096435188312 0.19836767814885078 0.5611136942374099 0.7920552521937324 0.8718862154746211 0.919128791559051 0.5944719286938169 0.6715233841166858 0.8134913108178033 0.31622325133706897 0.9312725384208438 0.3694657859887826 0.388719633197262 0.8444056935910591 0.37889793302096453";
	// 	testv(Parse.fromStringl(points));
	// }

	@Test
	public void test2()
	{
		String points = "0.5676456016302543 0.4518148943310536 0.1184598128419598 0.20067071659523006 0.9050607290000902 0.24802282637595485 0.17769487229123782 0.3204266734150384 0.4897548174228652 0.8158349822279298 0.4016361092368343 0.34863302196971296 0.833618334583889 0.4669839132452094 0.8226666717645541 0.2895543172696726 0.3738209584114033 0.43989868363781115 0.26931143983661 0.1946616649300407";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test3()
	{
		String points = "0.17768298796129514 0.7599111525996314 0.5246961032615671 0.21410659621608563 0.5827824433933175 0.06289277578455865 0.8943166190443155 0.5484553646208565 0.26573147580399664 0.061149891932656145 0.08904918837006001 0.947581414725041 0.7067715796484569 0.6681229191899847 0.5025548123965365 0.11661951283488675 0.9397620519678516 0.7319931472198214 0.3860544656444044 0.6483123860633365";
		testv(Parse.fromStringl(points));

	}

	@Test
	public void test4()
	{
		String points = "0.3412664453926514 0.43095585746978193 0.24520117847648754 0.2810206158715449 0.514129513049274 0.15582454019309475 0.12880437820145793 0.7201368613812935 0.46797792974858615 0.9480579956328551 0.6627893352001413 0.6433563834774074 0.5952347258488743 0.32382050906069876 0.6565819128824982 0.5921423917439721 0.5205843296617889 0.4313692922708718 0.051470949795757115 0.8126692403755158";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test5()
	{
		String points = "0.5618732891690869 0.3851316184450577 0.4622275117209256 0.8711212063638429 0.7379444003119875 0.1593970242813727 0.49637413194563323 0.9327544963187233 0.9389757518642577 0.7475962009947571 0.5083662369388156 0.3975134653783671 0.8163199172829131 0.4067781609515119 0.25388758989341265 0.3189762624122139 0.37593704863133826 0.41678974150427706 0.46838719067083306 0.16553079576704363";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test6()
	{
		String points = "0.8902820596978058 0.13576782503926563 0.06778759726390997 0.6339426902529797 0.13489094202919505 0.6031621978255544 0.8834844462041159 0.11684092875489763 0.74108998303024 0.1710562142560541 0.8219805950047233 0.07561423587271693 0.4109710089666826 0.3785182808852026 0.31237554578383553 0.4843195664175561 0.09193245030120292 0.19906044510789586 0.5348709740391395 0.41820795171867803";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test8()
	{
		String points = "0.5077671066141505 0.3733599350425685 0.188577059146893 0.37204207706831594 0.43033865851091313 0.26624810311003955 0.7254428093354808 0.051108437375196936 0.27866250624644007 0.4449342033422778 0.47333190956689586 0.4945881627470211 0.08393982978384246 0.7343540928134791 0.4967173193929795 0.1587888213085759 0.6774548245464102 0.6217094621861414 0.42551606996297203 0.1587889170492153";
		testv(Parse.fromStringl(points));
	}

	/* Input is corrected */
	// @Test
	// public void test9()
	// {
	// 	String points = "0.3698606065895067 0.828758631190339 0.7139376220245404 0.8287586676386947 0.30151929483926687 0.20028002181736013 0.8251200891263699 0.30024541957066797 0.39389753762469815 0.35761421745818817 0.6798112122976925 0.7490695808147612 0.4846030418316166 0.09701602655239791 0.5721016106403077 0.44101002076688756 0.5598508300447652 0.1428300788718551 0.5686929051950713 0.22407012321977376";
	// 	testv(Parse.fromStringl(points));
	// }

	/* Input is corrected */
	// @Test
	// public void test10()
	// {
	// 	String points = "0.7359260317573318 0.06566905064192372 0.6143684777870559 0.32016520745322036 0.13937524240799198 0.28709026627943934 0.9133714174146123 0.4591047113091928 0.7438886426511147 0.5964139826263822 0.355805090222597 0.3519182410119969 0.4694481515329005 0.654305524024003 0.40683393416386326 0.6603228139736068 0.9164891530694816 0.3635953705980377 0.7087693621186903 0.6603227250186889";
	// 	testv(Parse.fromStringl(points));
	// }

	@Test
	public void test11()
	{
		// Issue with vertical ?
		String points = "0.14728793691517492 0.2588414988810698 0.4229941031383244 0.43563459070442173 0.47999765035049735 0.5433260239574038 0.7436775361495065 0.562291933248124 0.842883434743062 0.7883092500828255 0.779035323393455 0.8305241654752722 0.3950428135821799 0.3383207094949421 0.24828802830961172 0.1170157319594817 0.6395971325054168 0.5622918866606449 0.2637302461956683 0.20091975105907625";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test12()
	{
		String points = "0.6444298001334152 0.3249625348036984 0.4545754187483231 0.18681065854226442 0.1759464853486173 0.3046503519350711 0.9173415163079661 0.31773398637340067 0.6036356250001067 0.532120105228489 0.1854728169155273 0.52304696233491 0.491137643882995 0.14095092203808568 0.36406346860050015 0.18646922424154597 0.464965882698994 0.20847323379189794 0.7226820405665182 0.6499970010287404";
		testv(Parse.fromStringl(points));
	}

	@Test
	public void test13()
	{
		String points = "0.9439185181839638 0.0774646868660891 0.051736574778830556 0.7412554989911696 0.29714088483701584 0.5396684893011165 0.43920444463057523 0.4517789723110318 0.12885537418292584 0.9000098376585777 0.42418951675726263 0.8903011326720588 0.48549941930204604 0.3550290765130251 0.8312805336422732 0.8104860422871404 0.5843102495113509 0.4173671729822798 0.3640312104446419 0.45177907122740896";
		testv(Parse.fromStringl(points));
	}

}
