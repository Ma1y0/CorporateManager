{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs =
    { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { inherit system; };
    in
    {
      devShell.${system} = pkgs.mkShell {
        packages = with pkgs; [
	  maven
	  jdk23
	  jdt-language-server
	  sqlitebrowser
        ];
        shellHook = ''
          exec zsh
        '';
      };
    };
}

