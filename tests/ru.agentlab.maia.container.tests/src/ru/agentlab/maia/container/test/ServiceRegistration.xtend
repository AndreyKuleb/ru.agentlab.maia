package ru.agentlab.maia.memory.context.test.blackbox

import java.util.ArrayList
import java.util.List

enum ServiceRegistration {

	NONE,
	SERVICE_BY_CLASS,
	SERVICE_BY_STRING

}

class ServiceRegistrationExtension {

	def static List<Pair<ServiceRegistration, ServiceRegistration>> combinations() {
		val result = new ArrayList<Pair<ServiceRegistration, ServiceRegistration>>
		for (serviceContext : ServiceRegistration.values) {
			for (serviceParent : ServiceRegistration.values) {
				result += serviceContext -> serviceParent
			}
		}
		return result
	}

}