import org.apache.commons.lang.WordUtils

includeTargets << new File( optimusPluginDir,
    'scripts/_OptimusUtils.groovy' )

target( createServiceGetLog:"Generate application logs for 'get' service method" ) {

    depends( checkVersion, configureProxy, bootstrap )
    def domainClassList = getDomainClassList( args )
    if ( !domainClassList ) return
    domainClassList.each { generate( it ) }
    def msg = "Finished generation of 'get' service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createServiceGetLog )

void generate( domainClass ) {

    def idAssigned = getIdAssigned( domainClass )
    def content = '' << "package ${domainClass.packageName}.aop\n\n"
    content << generateImports( domainClass.packageName, domainClass.name )
    content << generateClassDeclaration( domainClass.name )
    content << generatePointcutMethod( domainClass, idAssigned )
    content << generateBeforeMethod( idAssigned )
    content << generateAfterReturningMethod( domainClass.name, idAssigned )
    content << generateAfterThrowingMethod( idAssigned )
    content << '}'
    def directory = generateDirectory( "src/groovy",
        "${domainClass.packageName}.aop" )
    def fileName = "${domainClass.name}ServiceGet.groovy"
    new File(directory, fileName).text = content.toString()

}// End of method

String generateImports( packageName, className ) {

    def content = '' << "import ${packageName}.${className}\n\n"
    [ 'AfterReturning', 'AfterThrowing', 'Aspect', 'Before',
        'Pointcut' ].each {
        content << "import org.aspectj.lang.annotation.${it}\n"
    } // End of closure
    content << "\nimport org.springframework.stereotype.Component\n"
    content << "\n"
    content.toString()

}// End of method

String generateClassDeclaration( className ) {

    def content = '' << '@Component\n'
    content << '@Aspect\n'
    content << "class ${className}ServiceGet {\n\n"
    content.toString()

}// End of method

String generatePointcutMethod( domainClass, idAssigned ) {

    def className = domainClass.name
    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${TAB}@Pointcut(\n"
    content << "${TAB*2}value='execution(${domainClass.fullName} "
    content << "${domainClass.fullName}Service.get(..)) && bean"
    content << "(${classNameLower}Service) && args(${idName})',\n"
    content << "${TAB*2}argNames='${idName}')\n"
    content << "${TAB}public void getMethod( ${idType}"
    content << " ${idName} ) {}\n\n"
    content.toString()

}// End of method

String generateBeforeMethod( idAssigned ) {

    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${TAB}@Before('getMethod(${idName})')\n"
    content << "${TAB}void before( ${idType} ${idName} ) {\n"
    content << "${TAB*2}log.info( \"Begins request:\${${idName}}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterReturningMethod( className, idAssigned ) {

    def classNameLower = WordUtils.uncapitalize( className )
    def idName = idAssigned ? idAssigned.name : 'id'
    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${TAB}@AfterReturning(\n"
    content << "${TAB*2}pointcut='getMethod(${idType})',\n"
    content << "${TAB*2}returning='${classNameLower}')\n"
    content << "${TAB}void afterReturning( "
    content << "${className} ${classNameLower} ) {\n"
    content << "${TAB*2}log.info( \"End of request: \${${classNameLower}}\" )\n"
    content << "${TAB}}\n\n"
    content.toString()

}// End of method

String generateAfterThrowingMethod( idAssigned ) {

    def idType = idAssigned ? idAssigned.type : 'Long'
    def content = '' << "${TAB}@AfterThrowing(\n"
    content << "${TAB*2}pointcut='getMethod(${idType})',\n"
    content << "${TAB*2}throwing='e' )\n"
    content << "${TAB}void afterThrowing( Exception e ) {\n\n"
    content << "${TAB*2}def message = ''\n"
    content << "${TAB*2}message << \"Error in request\"\n"
    content << "${TAB*2}message << \":"
    content << " \${e.class.simpleName} - \${e.message}\"\n"
    content << "${TAB*2}log.info( message.toString() )\n"
    content << "\n${TAB}}\n\n"
    content.toString()

}// End of method
