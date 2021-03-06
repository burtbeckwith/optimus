includeTargets << new File( optimusPluginDir,
    'scripts/_CreateServiceLogs.groovy' )

target( createLogs:'Generate application logs' ) {

    depends( checkVersion, configureProxy, bootstrap,
        createServiceLogs )
    def msg = "Finished generation of service logs"
    event( 'StatusFinal', [ msg ] )

}// End of closure

setDefaultTarget( createLogs )
