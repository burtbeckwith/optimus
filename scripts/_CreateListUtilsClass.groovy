target( createListUtils:'Generate ListUtils class' ) {

    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    generate( domainClassList[ 0 ] )
    def msg = "Finished generation of ListUtils class file"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createListUtils )

void generate( domainClass ) {

    def content = '' << "package ${domainClass.packageName}\n\n"
    content << "class ListUtils {\n\n"
    content << generateParseMaxMethod()
    content << generateParseOffsetMethod()
    content << generateParseOrderMethod()
    content << generateParseSortMethod()
    content << '}'
    def directory = generateDirectory( "src/groovy", domainClass.packageName )
    new File(directory, "ListUtils.groovy").text = content.toString()

}// End of method

String generateParseMaxMethod() {

    def content = new StringBuilder()
    content << "${TAB}static Integer parseMax( String max ) {\n\n"
    content << "${TAB*2}if ( max?.isInteger() && max != '0' ) {\n"
    content << "${TAB*3}def maxInteger = new Integer( max )\n"
    content << "${TAB*3}return Math.min( maxInteger, 10 )\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}10\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateParseOffsetMethod() {

    def content = new StringBuilder()
    content << "${TAB}static Integer parseOffset("
    content << " String offset ) {\n\n"
    content << "${TAB*2}if ( offset?.isInteger() ) {\n"
    content << "${TAB*3}return new Integer( offset )\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}null\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateParseOrderMethod() {

    def content = new StringBuilder()
    content << "${TAB}static String parseOrder("
    content << " String order ) {\n\n"
    content << "${TAB*2}if ( order == 'asc' ||"
    content << " order == 'desc' ) {\n"
    content << "${TAB*3}return order\n"
    content << "${TAB*2}}\n"
    content << "${TAB*2}null\n\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateParseSortMethod() {

    def content = new StringBuilder()
    content << "${TAB}static String parseSort("
    content << " sort, fields ) {\n"
    content << "${TAB*2}fields.find { it == sort }\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method
