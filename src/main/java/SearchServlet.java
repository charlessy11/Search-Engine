import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author Charles
 *
 */
public class SearchServlet extends HttpServlet {

	/**
	 * Initializes stemmer to be used
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	/** The title to use for this webpage. */
	private static final String TITLE = "SearchX";

	/** Everything that will be output after a search is carried out. */
	private LinkedList<String> output;

	private InvertedIndexBuilder builder;

	private InvertedIndex index;
//	/**
//	 * The number of searches
//	 */
//	private int searches = 0;

	/**
	 * The time it took
	 */
	private double seconds;

	private QueryResultBuilderInterface queryBuilder;
	
//	private WebCrawler crawler;

	/**
	 * @param queryBuilder
	 * @param index
	 */
	public SearchServlet(QueryResultBuilderInterface queryBuilder, InvertedIndex index, InvertedIndexBuilder builder) {
		super();
		output = new LinkedList<>();
		this.queryBuilder = queryBuilder;
		this.index = index;
		this.builder = builder;
//		this.crawler = crawler;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		LinkedList<String> output = new LinkedList<>();
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>");	
		out.printf("<head>");
		out.printf("<meta charset=\"utf-8\">");
		//displays title on tab button
		out.printf("<title>%s</title>%n", TITLE);
		out.printf("</head>%n");

		//formats title
		out.printf("<body>");
		//displays title on screen
		out.printf("<div class=\"center container\" style=\"text-align:center\">%n");
		out.printf("<h1>" + TITLE + "</h1>");
		out.printf("<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		//displays search bar
		out.printf("<input class=\"input\" size=\"100\" type=\"text\" name=\"%s\" placeholder=\"Search something..\">", "search");
		//displays search button
		out.printf("<div class=\"control\">");
		out.printf("<button class=\"button is-primary\" onclick=\\\"clicked()\\\" name=\\\"enter\\\" type=\\\"submit\\\">");
		out.printf("Search");
		out.printf("</button>" + "</form>");
		if (!output.isEmpty()) {
			for (String query : output) {
				out.printf("<div class=\"box\">" + query + "</div>");
//				out.printf("CHECK");
			}
		} else {
//			out.printf("<div class=\"content has-text-centered\">%n");
			out.printf("output is empty");
		}
//		out.printf("<footer class=\"footer\">%n");
//		out.printf("<div class=\"content has-text-centered\">%n");
//		out.printf("</div>");
//		out.printf("</footer>");
		out.printf("</body>");	
		out.printf("</html>");
		
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		LinkedList<String> output = new LinkedList<>();

		response.setContentType("text/html");

		String query = request.getParameter("search");
		String formatted;
		
		// avoid xss attacks using apache commons text
		query = StringEscapeUtils.escapeHtml4(query);
		
		if (query != null && !query.isBlank()) {	
			SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
			Set<String> queryList = new HashSet<String>();
			for (String word : query.split(" ")) {
				System.out.println("Word: " + word);
				queryList.add((stemmer.stem(word.toLowerCase())).toString());
//				System.out.println("List: " + queryList);
			}
			System.out.println("List: " + queryList);
			Collection<InvertedIndex.SingleSearchResult> results = this.index.partialSearch(queryList);
			System.out.println("Results: " + results);
			
			if (results.isEmpty()) {
				output.clear();
				
				formatted = String.format("The query line " + '"' + request.getParameter("search") + '"' + " was not found.");
				output.add(formatted);
			} else {
				output.clear();
				
				for (InvertedIndex.SingleSearchResult result : results) {
//					String score = String.format("%.3f", result.getScore());
					formatted = String.format("<a href=\"%s\">%s</a>", result.getLocation(), result.getLocation()); 
//												 "score: " + score, "matches: " + result.getMatches());
					output.add(formatted);
				}
			}
		} else {
			output.clear();
			formatted = "Nothing was searched.";
			output.add(formatted);
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
		response.flushBuffer();
	}
}