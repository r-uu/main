module de.ruu.lib.docker.health
{
	requires static lombok;
	requires org.slf4j;
	requires java.sql;

	exports de.ruu.lib.docker.health;
	exports de.ruu.lib.docker.health.check;
	exports de.ruu.lib.docker.health.fix;
}
