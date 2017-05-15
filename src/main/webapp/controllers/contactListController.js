var contactListController;

contactListController = function($scope, $http) {
	$scope.contacts = [];
	$scope.preDeletedContact = {};

	$scope.init = function() {
		$scope.listAllContacts();
	};
	
	$scope.listAllContacts = function() {

		// Chamar o servlet /contacts com um método 'GET' para listar os
		// contatos do banco de dados.
		$http.get('/contacts')
		.success(function(data) {
			$scope.contacts = data;
		}).error(function(data, status) {
			console.log(status, data);
		});
	};

	$scope.preDelete = function(contact) {
		$scope.preDeletedContact = contact;
		$('#myModal').modal('show');
	};

	$scope.delete = function() {
		if($scope.preDeletedContact != null) {

			// Chamar o servlet /contacts com um método 'DELETE' para deletar um
			// contato do banco de dados passando um parâmetro de identificação.
			$http.delete('/contacts', {data:{id: $scope.preDeletedContact.id}})
			.success(function(data) {
				var index = $scope.contacts.indexOf($scope.preDeletedContact);
				$scope.contacts.splice(index, 1);
				$scope.preDeletedContact = null;
				$('#myModal').modal('hide');
			}).error(function(data, status) {
				console.log(status, data);
			});
		}
	};

	$scope.bday = function(c) {
		if(c.birthDay==null || c.birthDay == ""){
			return "";
		} else {
			return c.birthDay + "/" + c.birthMonth + "/" + c.birthYear;
		}
	};
};

angular.module('avaliacandidatos').controller("contactListController", contactListController);