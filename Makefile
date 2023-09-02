PACT_CLI="docker run --rm -v ${PWD}:${PWD} -e PACT_BROKER_URL -e PACT_BROKER_TOKEN pactfoundation/pact-cli"

record_deployment:
	@"${PACT_CLI}" pact-broker record_deployment --pacticipant ${PACTICIPANT} --version ${GIT_COMMIT} --environment production --broker-base-url=${PACT_BROKER_URL} --broker-token=${PACT_BROKER_TOKEN}