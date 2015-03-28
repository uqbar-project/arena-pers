package uqbar.arena.persistence.reflection

import java.io.File
import java.net.URLClassLoader

class ClasspathCrawler(classLoader: ClassLoader) {
  def getClasses(packageName: String): Set[Class[_]] = {
    Set()
  }

  lazy val classpaths: List[String] = {
    System.getProperty("java.class.path").split(File.pathSeparatorChar + "").toList ++
      ifURLClassLoader { cl => classpathFromURLClassLoader(cl) } { _ => Nil }
  }

  def ifURLClassLoader[T](ifTrue: URLClassLoader => T)(ifFalse: ClassLoader => T): T = {
    classLoader match {
      case cl:URLClassLoader => ifTrue(cl)
      case cl => ifFalse(cl)
    }
  }

  def classpathFromURLClassLoader(cl: URLClassLoader) =
    (cl.getURLs().map { u =>
      val path = u.toString()
      if (path.startsWith("file"))
        path.substring(5)
      else
        path
    }).toList
}