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
 * Creates a search servlet 
 * 
 * @author Charles
 *
 */
public class SearchServlet extends HttpServlet {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes stemmer to be used
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	/** 
	 * The title to use for this webpage
	 */
	private static final String TITLE = "SearchX";

	/** 
	 * A list of output results from a partial search
	 */
	private LinkedList<String> output;

	/**
	 * The inverted index
	 */
	private InvertedIndex invertedIndex;

	/**
	 * @param invertedIndex the inverted index
	 */
	public SearchServlet(InvertedIndex invertedIndex) {
		super();
		output = new LinkedList<>();
		this.invertedIndex = invertedIndex;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		//Displays a webpage with a text box where users may enter a multi-word search query 
		//and click a button that submits that query to a servlet
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
			}
		} 
		out.printf("</body>");	
		out.printf("</html>");
		
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		String query = request.getParameter("search");
		String formatted;
		
		// avoid xss attacks using apache commons text
		query = StringEscapeUtils.escapeHtml4(query);
		
		//checks if query line is given by  user
		if (query != null && !query.isBlank()) {	
			SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
			Set<String> querySet = new HashSet<String>();
			for (String word : query.split(" ")) {
				//add each cleaned and stemmed word to a set
				querySet.add((stemmer.stem(word.toLowerCase())).toString());
			}
			//perform partial search on the queries in the set and save the results in a list
			Collection<InvertedIndex.SingleSearchResult> results = this.invertedIndex.partialSearch(querySet);
			//checks if results list is empty
			if (results.isEmpty()) {
				output.clear();
				formatted = String.format("The query line " + '"' + request.getParameter("search") + '"' + " was not found.");
				output.add(formatted);
			} 
			//checks if results list contains any partial search results
			else {
				output.clear();
				for (InvertedIndex.SingleSearchResult result : results) {
					//displays the search results as dynamically generated HTML with sorted and clickable links
					formatted = String.format("<a href=\"%s\">%s</a>", result.getLocation(), result.getLocation()); 
					output.add(formatted);
				}
			}
		} 
		//checks if submit button was clicked without a query line
		else {
			output.clear();
			formatted = "Nothing was searched.";
			output.add(formatted);
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
		response.flushBuffer();
	}
}