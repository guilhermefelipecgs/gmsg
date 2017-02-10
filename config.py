def can_build(plat):
	return plat=="android"

def configure(env):
	if env['platform'] == 'android':
		env.android_add_java_dir("src")
		env.android_add_dependency("compile 'com.google.android.gms:play-services-games:10.0.0'")
		env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:10.0.0'")
		env.android_add_to_manifest("src/AndroidManifestChunk.xml")

