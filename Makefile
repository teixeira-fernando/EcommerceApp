PACT_CLI="docker run --rm -v ${PWD}:${PWD} -e PACT_BROKER_URL -e PACT_BROKER_TOKEN pactfoundation/pact-cli"

run-pact-consumer-tests:
	mvn -f shop/ -Dtest=**/contract/*ConsumerPact test -DfailIfNoTests=false
	mvn -f shipment/ -Dtest=**/contract/*ConsumerPact test -DfailIfNoTests=false

publish-pacts:
	mvn -f shop/ pact:publish -Dpact.publish.consumer.version=${GIT_COMMIT} -Dpact.publish.consumer.branchName=${GIT_BRANCH} -Dpact.consumer.tags=${GIT_BRANCH}
	mvn -f shipment/ pact:publish -Dpact.publish.consumer.version=${GIT_COMMIT} -Dpact.publish.consumer.branchName=${GIT_BRANCH} -Dpact.consumer.tags=${GIT_BRANCH}

run-pact-provider-tests:
	mvn -f inventory/ -Dtest=**/contract/*ProviderPact test -DfailIfNoTests=false -Dpact.provider.version=${GIT_COMMIT} -Dpact.provider.branch=${GIT_BRANCH} -Dpact.provider.tag=${GIT_BRANCH}
	mvn -f shop/ -Dtest=**/contract/*ProviderPact test -DfailIfNoTests=false -Dpact.provider.version=${GIT_COMMIT} -Dpact.provider.branch=${GIT_BRANCH} -Dpact.provider.tag=${GIT_BRANCH}

record_deployment:
	@"${PACT_CLI}" pact-broker record_deployment --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --environment production --broker-base-url=${PACT_BROKER_URL} --broker-token=${PACT_BROKER_TOKEN}

publish_all_packages:
	mvn -f inventory/ deploy -s settings.xml -Dserver.github.password=${GITHUB_TOKEN}
	mvn -f shop/ deploy -s settings.xml -Dserver.github.password=${GITHUB_TOKEN}
	mvn -f shipment/ deploy -s settings.xml -Dserver.github.password=${GITHUB_TOKEN}
	mvn -f base-domain/ deploy -s settings.xml -Dserver.github.password=${GITHUB_TOKEN}